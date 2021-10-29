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
import io.verik.compiler.common.Cardinal
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.impl.AbstractTypeAliasDescriptor
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.calls.components.isVararg
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

object CoreDeclarationMap {

    private val constructorMap = HashMap<CoreClassDeclaration, CoreConstructorDeclaration>()
    private val functionMap = HashMap<String, ArrayList<CoreFunctionDeclaration>>()
    private val declarationMap = HashMap<String, CoreDeclaration>()

    init {
        addCoreDeclarations(Core::class)
    }

    operator fun get(
        castContext: CastContext,
        declarationDescriptor: DeclarationDescriptor,
        element: KtElement
    ): Declaration? {
        return when (declarationDescriptor) {
            is ClassConstructorDescriptor -> getConstructor(castContext, declarationDescriptor, element)
            is SimpleFunctionDescriptor -> getFunction(castContext, declarationDescriptor, element)
            else -> getDeclaration(declarationDescriptor)
        }
    }

    private fun addCoreDeclarations(kClass: KClass<*>) {
        val kClassInstance = kClass.objectInstance!!
        kClass.declaredMemberProperties.forEach {
            @Suppress("UNCHECKED_CAST")
            val property = (it as KProperty1<Any, *>).get(kClassInstance) as CoreDeclaration
            when (property) {
                is CoreConstructorDeclaration ->
                    constructorMap[property.classDeclaration] = property
                is CoreFunctionDeclaration -> {
                    val qualifiedName = property.getQualifiedName()
                    if (qualifiedName !in functionMap)
                        functionMap[qualifiedName] = ArrayList()
                    functionMap[qualifiedName]!!.add(property)
                }
                else ->
                    declarationMap[property.getQualifiedName()] = property
            }
        }
        kClass.nestedClasses.forEach { addCoreDeclarations(it) }
    }

    private fun getConstructor(
        castContext: CastContext,
        descriptor: ClassConstructorDescriptor,
        element: KtElement
    ): CoreConstructorDeclaration? {
        val reference = castContext.getDeclaration(
            descriptor.returnType.constructor.declarationDescriptor!!,
            element
        )
        return constructorMap[reference]
    }

    private fun getFunction(
        castContext: CastContext,
        descriptor: SimpleFunctionDescriptor,
        element: KtElement
    ): CoreFunctionDeclaration? {
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

    // TODO general matching for type parameterized functions
    private fun matchFunction(
        castContext: CastContext,
        descriptor: SimpleFunctionDescriptor,
        element: KtElement,
        function: CoreFunctionDeclaration
    ): Boolean {
        val valueParameters = descriptor.valueParameters
        val parameterClassNames = function.parameterClassDeclarations.map { it.name }
        if (valueParameters.size != parameterClassNames.size)
            return false
        valueParameters.zip(parameterClassNames).forEach { (valueParameter, parameterClassName) ->
            if (parameterClassName != Core.Kt.C_Any.name) {
                val type = if (valueParameter.isVararg) {
                    castContext.castType(valueParameter.varargElementType!!, element)
                } else {
                    castContext.castType(valueParameter.type, element)
                }
                if (type.reference !is CoreClassDeclaration)
                    return false
                if (type.reference.name != parameterClassName)
                    return false
            }
        }
        return true
    }

    private fun getDeclaration(descriptor: DeclarationDescriptor): Declaration? {
        val qualifiedName = descriptor.fqNameOrNull()?.asString()
            ?: return null
        val declaration = declarationMap[qualifiedName]
        return when {
            declaration != null -> declaration
            descriptor is AbstractTypeAliasDescriptor -> {
                val name = descriptor.name.asString()
                if (name == "*") {
                    Cardinal.UNRESOLVED
                } else {
                    val cardinal = name.toIntOrNull()
                    if (cardinal != null)
                        Cardinal.of(cardinal)
                    else
                        null
                }
            }
            qualifiedName == "${CorePackage.VK.name}.Cardinal" -> Cardinal.UNRESOLVED
            else -> null
        }
    }
}
