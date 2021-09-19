/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.verik.compiler.core.common

import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.cast.CastContext
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.impl.AbstractTypeAliasDescriptor
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties

object CoreDeclarationMap {

    private val declarationMap = HashMap<String, CoreDeclaration>()
    private val functionMap = HashMap<String, ArrayList<CoreKtFunctionDeclaration>>()

    init {
        addCoreDeclarations(Core::class)
    }

    operator fun get(
        castContext: CastContext,
        declarationDescriptor: DeclarationDescriptor,
        element: KtElement
    ): Declaration? {
        return when (declarationDescriptor) {
            is SimpleFunctionDescriptor -> getFunction(castContext, declarationDescriptor, element)
            else -> getDeclaration(declarationDescriptor)
        }
    }

    private fun addCoreDeclarations(kClass: KClass<*>) {
        val kClassInstance = kClass.objectInstance
        if (kClassInstance is CoreScope) {
            kClass.memberProperties.forEach {
                if (it.returnType.isSubtypeOf(CoreDeclaration::class.createType())) {
                    @Suppress("UNCHECKED_CAST")
                    val property = (it as KProperty1<Any, *>).get(kClassInstance) as CoreDeclaration
                    if (property.qualifiedName != "${kClassInstance.parent}.${property.name}") {
                        val expectedString =
                            "Expected ${kClassInstance.parent}.${property.name} actual ${property.qualifiedName}"
                        throw IllegalArgumentException("Qualified name does not match scope parent: $expectedString")
                    }
                    when (property) {
                        is CoreAbstractFunctionDeclaration -> {
                            if (property is CoreKtFunctionDeclaration) {
                                if (property.qualifiedName !in functionMap)
                                    functionMap[property.qualifiedName] = ArrayList()
                                functionMap[property.qualifiedName]!!.add(property)
                            }
                        }
                        else ->
                            declarationMap[property.qualifiedName] = property
                    }
                }
            }
        }
        kClass.nestedClasses.forEach { addCoreDeclarations(it) }
    }

    private fun getFunction(
        castContext: CastContext,
        descriptor: SimpleFunctionDescriptor,
        element: KtElement
    ): CoreDeclaration? {
        val qualifiedName = descriptor.fqNameOrNull()?.asString()
            ?: return null
        val functions = functionMap[qualifiedName]
            ?: return null
        functions.forEach {
            if (matchFunction(castContext, descriptor, element, it))
                return it
        }
        return null
    }

    private fun matchFunction(
        castContext: CastContext,
        descriptor: SimpleFunctionDescriptor,
        element: KtElement,
        function: CoreKtFunctionDeclaration
    ): Boolean {
        val valueParameters = descriptor.valueParameters
        val parameterClassNames = function.parameterClassNames
        if (valueParameters.size != parameterClassNames.size)
            return false
        valueParameters.zip(parameterClassNames).forEach { (valueParameter, parameterClassName) ->
            val type = castContext.castType(valueParameter.type, element)
            if (type.reference !is CoreClassDeclaration)
                return false
            if (type.reference.name != parameterClassName)
                return false
        }
        return true
    }

    private fun getDeclaration(descriptor: DeclarationDescriptor): CoreDeclaration? {
        val qualifiedName = descriptor.fqNameOrNull()?.asString()
            ?: return null
        val declaration = declarationMap[qualifiedName]
        return when {
            declaration != null -> declaration
            descriptor is AbstractTypeAliasDescriptor -> {
                val name = descriptor.name.asString()
                if (name == "*") {
                    CoreCardinalUnresolvedDeclaration
                } else {
                    val cardinal = name.toIntOrNull()
                    if (cardinal != null)
                        Core.Vk.cardinalOf(cardinal)
                    else
                        null
                }
            }
            else -> null
        }
    }
}
