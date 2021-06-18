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

import io.verik.compiler.ast.common.Name
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.impl.AbstractTypeAliasDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberProperties

object CoreDeclarationMap {

    private val declarationMap = HashMap<Name, CoreDeclaration>()
    private val functionMap = HashMap<Name, ArrayList<CoreFunctionDeclaration>>()

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

    operator fun get(descriptor: DeclarationDescriptor): CoreDeclaration? {
        return when (descriptor) {
            is SimpleFunctionDescriptor -> getFunction(descriptor)
            else -> getDeclaration(descriptor)
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
            if (it.returnType == CoreFunctionDeclaration::class.createType()) {
                @Suppress("UNCHECKED_CAST")
                val property = (it as KProperty1<Any, *>).get(coreScope) as CoreFunctionDeclaration
                if (property.qualifiedName !in functionMap)
                    functionMap[property.qualifiedName] = ArrayList()
                functionMap[property.qualifiedName]!!.add(property)
            }
        }
    }

    private fun getFunction(descriptor: SimpleFunctionDescriptor): CoreDeclaration? {
        val qualifiedName = getQualifiedName(descriptor)
            ?: return null
        val functions = functionMap[qualifiedName]
            ?: return null
        functions.forEach {
            if (matchFunction(descriptor, it))
                return it
        }
        return null
    }

    private fun matchFunction(descriptor: SimpleFunctionDescriptor, function: CoreFunctionDeclaration): Boolean {
        val valueParameters = descriptor.valueParameters
        val expectedParameterClassNames = function.parameterClassNames
        if (valueParameters.size != expectedParameterClassNames.size)
            return false
        valueParameters.zip(expectedParameterClassNames).forEach { (valueParameter, expectedParameterClassName) ->
            val parameterClassName = Name(valueParameter.type.getJetTypeFqName(false).substringAfterLast("."))
            if (parameterClassName != expectedParameterClassName)
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
                val cardinal = nameString.toIntOrNull()
                if (cardinal != null) {
                    CoreCardinalConstantDeclaration(cardinal)
                } else null
            }
            else -> null
        }
    }

    private fun getQualifiedName(descriptor: DeclarationDescriptor): Name? {
        return descriptor.fqNameOrNull()?.let { Name(it.toString()) }
    }
}