/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.importer.common

import io.verik.importer.ast.kt.element.KtClass
import io.verik.importer.ast.kt.element.KtConstructor
import io.verik.importer.ast.kt.element.KtDeclaration
import io.verik.importer.ast.kt.element.KtElement
import io.verik.importer.ast.kt.element.KtEnum
import io.verik.importer.ast.kt.element.KtEnumEntry
import io.verik.importer.ast.kt.element.KtFile
import io.verik.importer.ast.kt.element.KtFunction
import io.verik.importer.ast.kt.element.KtPackage
import io.verik.importer.ast.kt.element.KtProject
import io.verik.importer.ast.kt.element.KtProperty
import io.verik.importer.ast.kt.element.KtValueParameter

abstract class KtVisitor {

    open fun visitElement(element: KtElement) {}

    open fun visitProject(project: KtProject) {
        visitElement(project)
    }

    open fun visitFile(file: KtFile) {
        visitElement(file)
    }

    open fun visitDeclaration(declaration: KtDeclaration) {
        visitElement(declaration)
    }

    open fun visitPackage(`package`: KtPackage) {
        visitDeclaration(`package`)
    }

    open fun visitClass(`class`: KtClass) {
        visitDeclaration(`class`)
    }

    open fun visitEnum(enum: KtEnum) {
        visitDeclaration(enum)
    }

    open fun visitFunction(function: KtFunction) {
        visitDeclaration(function)
    }

    open fun visitConstructor(constructor: KtConstructor) {
        visitDeclaration(constructor)
    }

    open fun visitProperty(property: KtProperty) {
        visitDeclaration(property)
    }

    open fun visitValueParameter(valueParameter: KtValueParameter) {
        visitDeclaration(valueParameter)
    }

    open fun visitEnumEntry(enumEntry: KtEnumEntry) {
        visitDeclaration(enumEntry)
    }
}
