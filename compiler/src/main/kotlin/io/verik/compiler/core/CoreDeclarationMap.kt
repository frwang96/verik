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
        CoreClass::class.memberProperties.forEach {
            val property = it.get(CoreClass)
            if (property is CoreClassDeclaration)
                declarationMap[property.qualifiedName] = property
        }

        CoreCardinal::class.memberProperties.forEach {
            val property = it.get(CoreCardinal)
            if (property is CoreCardinalFunctionDeclaration)
                declarationMap[property.qualifiedName] = property
        }

        CoreFunction::class.nestedClasses.forEach { packageClass ->
            packageClass.nestedClasses.forEach { classClass ->
                val classObject = classClass.objectInstance!!
                classObject::class.memberProperties.forEach {
                    if (it.returnType == CoreFunctionDeclaration::class.createType()) {
                        @Suppress("UNCHECKED_CAST")
                        val property = (it as KProperty1<Any, *>).get(classObject) as CoreFunctionDeclaration
                        if (property.qualifiedName !in functionMap)
                            functionMap[property.qualifiedName] = ArrayList()
                        functionMap[property.qualifiedName]!!.add(property)
                    }
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
        val expectedParameterTypeNames = function.parameterTypeNames
        if (valueParameters.size != expectedParameterTypeNames.size)
            return false
        valueParameters.zip(expectedParameterTypeNames).forEach { (valueParameter, expectedParameterTypeName) ->
            val parameterTypeName = Name(valueParameter.type.getJetTypeFqName(false).substringAfterLast("."))
            if (parameterTypeName != expectedParameterTypeName)
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