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

package io.verik.compiler.specialize

import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtEnumEntry
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.element.kt.EPrimaryConstructor
import io.verik.compiler.common.TreeVisitor

class SpecializerDeclarationIndexerVisitor(
    private val specializerContext: SpecializerContext
) : TreeVisitor() {

    override fun visitKtBasicClass(basicClass: EKtBasicClass) {
        super.visitKtBasicClass(basicClass)
        val specializedBasicClass = EKtBasicClass(basicClass.location, basicClass.name)
        specializerContext.set(basicClass, specializedBasicClass)
    }

    override fun visitKtFunction(function: EKtFunction) {
        super.visitKtFunction(function)
        val specializedFunction = EKtFunction(function.location, function.name)
        specializerContext.set(function, specializedFunction)
    }

    override fun visitPrimaryConstructor(primaryConstructor: EPrimaryConstructor) {
        super.visitPrimaryConstructor(primaryConstructor)
        val specializedPrimaryConstructor = EPrimaryConstructor(primaryConstructor.location)
        specializerContext.set(primaryConstructor, specializedPrimaryConstructor)
    }

    override fun visitKtProperty(property: EKtProperty) {
        super.visitKtProperty(property)
        val specializedProperty = EKtProperty(property.location, property.name)
        specializerContext.set(property, specializedProperty)
    }

    override fun visitKtEnumEntry(enumEntry: EKtEnumEntry) {
        super.visitKtEnumEntry(enumEntry)
        val specializedEnumEntry = EKtEnumEntry(enumEntry.location, enumEntry.name)
        specializerContext.set(enumEntry, specializedEnumEntry)
    }

    override fun visitKtValueParameter(valueParameter: EKtValueParameter) {
        super.visitKtValueParameter(valueParameter)
        val specializedValueParameter = EKtValueParameter(valueParameter.location, valueParameter.name)
        specializerContext.set(valueParameter, specializedValueParameter)
    }
}
