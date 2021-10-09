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

package io.verik.compiler.copy

import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtEnumEntry
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.element.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.kt.ETypeAlias
import io.verik.compiler.common.TreeVisitor

class CopierDeclarationIndexerVisitor(
    private val copyContext: CopyContext
) : TreeVisitor() {

    override fun visitTypeAlias(typeAlias: ETypeAlias) {
        super.visitTypeAlias(typeAlias)
        val copiedTypeAlias = ETypeAlias(typeAlias.location, typeAlias.name)
        copyContext.set(typeAlias, copiedTypeAlias)
    }

    override fun visitTypeParameter(typeParameter: ETypeParameter) {
        super.visitTypeParameter(typeParameter)
        val copiedTypeParameter = ETypeParameter(typeParameter.location, typeParameter.name)
        copyContext.set(typeParameter, copiedTypeParameter)
    }

    override fun visitKtBasicClass(basicClass: EKtBasicClass) {
        super.visitKtBasicClass(basicClass)
        val copiedBasicClass = EKtBasicClass(basicClass.location, basicClass.name)
        copyContext.set(basicClass, copiedBasicClass)
    }

    override fun visitKtFunction(function: EKtFunction) {
        super.visitKtFunction(function)
        val copiedFunction = EKtFunction(function.location, function.name)
        copyContext.set(function, copiedFunction)
    }

    override fun visitPrimaryConstructor(primaryConstructor: EPrimaryConstructor) {
        super.visitPrimaryConstructor(primaryConstructor)
        val copiedPrimaryConstructor = EPrimaryConstructor(primaryConstructor.location)
        copyContext.set(primaryConstructor, copiedPrimaryConstructor)
    }

    override fun visitKtProperty(property: EKtProperty) {
        super.visitKtProperty(property)
        val copiedProperty = EKtProperty(property.location, property.name)
        copyContext.set(property, copiedProperty)
    }

    override fun visitKtEnumEntry(enumEntry: EKtEnumEntry) {
        super.visitKtEnumEntry(enumEntry)
        val copiedEnumEntry = EKtEnumEntry(enumEntry.location, enumEntry.name)
        copyContext.set(enumEntry, copiedEnumEntry)
    }

    override fun visitKtValueParameter(valueParameter: EKtValueParameter) {
        super.visitKtValueParameter(valueParameter)
        val copiedValueParameter = EKtValueParameter(valueParameter.location, valueParameter.name)
        copyContext.set(valueParameter, copiedValueParameter)
    }
}
