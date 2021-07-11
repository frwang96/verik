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

package io.verik.compiler.core

import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.ast.property.Name
import io.verik.compiler.cast.DeclarationMap
import io.verik.compiler.cast.TypeCaster
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.impl.AbstractTypeAliasDescriptor
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberProperties

object CoreDeclarationMap {

    private val declarationMap = HashMap<Name, CoreDeclaration>()
    private val functionMap = HashMap<Name, ArrayList<CoreKtFunctionDeclaration>>()

    init {
        CoreClass::class.nestedClasses.forEach { packageClass ->
            val packageCoreScope = packageClass.objectInstance
            if (packageCoreScope is CoreScope) addCoreClasses(packageCoreScope)
        }

        CoreCardinal::class.memberProperties.forEach {
            val property = it.get(CoreCardinal)
            if (property is CoreCardinalFunctionDeclaration)
                declarationMap[property.qualifiedName] = property
        }

        CoreFunction::class.nestedClasses.forEach { packageClass ->
            val packageCoreScope = packageClass.objectInstance
            if (packageCoreScope is CoreScope) {
                addCoreFunctions(packageCoreScope)
                packageCoreScope::class.nestedClasses.forEach { classClass ->
                    val classCoreScope = classClass.objectInstance
                    if (classCoreScope is CoreScope) addCoreFunctions(classCoreScope)
                }
            }
        }
    }

    operator fun get(
        declarationMap: DeclarationMap,
        declarationDescriptor: DeclarationDescriptor,
        element: KtElement
    ): Declaration? {
        return when (declarationDescriptor) {
            is SimpleFunctionDescriptor -> getFunction(declarationMap, declarationDescriptor, element)
            else -> getDeclaration(declarationDescriptor)
        }
    }

    private fun addCoreClasses(coreScope: CoreScope) {
        coreScope::class.memberProperties.forEach {
            if (it.returnType == CoreClassDeclaration::class.createType()) {
                @Suppress("UNCHECKED_CAST")
                val property = (it as KProperty1<Any, *>).get(coreScope) as CoreClassDeclaration
                declarationMap[property.qualifiedName] = property
            }
        }
    }

    private fun addCoreFunctions(coreScope: CoreScope) {
        coreScope::class.memberProperties.forEach {
            if (it.returnType == CoreKtFunctionDeclaration::class.createType()) {
                @Suppress("UNCHECKED_CAST")
                val property = (it as KProperty1<Any, *>).get(coreScope) as CoreKtFunctionDeclaration
                if (property.qualifiedName !in functionMap)
                    functionMap[property.qualifiedName] = ArrayList()
                functionMap[property.qualifiedName]!!.add(property)
            }
        }
    }

    private fun getFunction(
        declarationMap: DeclarationMap,
        descriptor: SimpleFunctionDescriptor,
        element: KtElement
    ): CoreDeclaration? {
        val qualifiedName = getQualifiedName(descriptor)
            ?: return null
        val functions = functionMap[qualifiedName]
            ?: return null
        functions.forEach {
            if (matchFunction(declarationMap, descriptor, element, it))
                return it
        }
        return null
    }

    private fun matchFunction(
        declarationMap: DeclarationMap,
        descriptor: SimpleFunctionDescriptor,
        element: KtElement,
        function: CoreKtFunctionDeclaration
    ): Boolean {
        val valueParameters = descriptor.valueParameters
        val parameterClassNames = function.parameterClassNames
        if (valueParameters.size != parameterClassNames.size)
            return false
        valueParameters.zip(parameterClassNames).forEach { (valueParameter, parameterClassName) ->
            val type = TypeCaster.castFromType(declarationMap, valueParameter.type, element)
            if (type.reference !is CoreClassDeclaration)
                return false
            if (type.reference.name != parameterClassName)
                return false
        }
        return true
    }

    private fun getDeclaration(descriptor: DeclarationDescriptor): CoreDeclaration? {
        val qualifiedName = getQualifiedName(descriptor)
            ?: return null
        val declaration = declarationMap[qualifiedName]
        return when {
            declaration != null -> declaration
            descriptor is AbstractTypeAliasDescriptor -> {
                val nameString = descriptor.name.toString()
                if (nameString == "*") {
                    CoreClass.Core.CARDINAL
                } else {
                    val cardinal = nameString.toIntOrNull()
                    if (cardinal != null) {
                        CoreCardinalConstantDeclaration(cardinal)
                    } else null
                }
            }
            else -> null
        }
    }

    private fun getQualifiedName(descriptor: DeclarationDescriptor): Name? {
        return descriptor.fqNameOrNull()?.let { Name(it.toString()) }
    }
}