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

package io.verik.importer.common

import io.verik.importer.ast.element.EAbstractPackage
import io.verik.importer.ast.element.EAbstractProperty
import io.verik.importer.ast.element.EDeclaration
import io.verik.importer.ast.element.EElement
import io.verik.importer.ast.element.EModule
import io.verik.importer.ast.element.EPort
import io.verik.importer.ast.element.EProject
import io.verik.importer.ast.element.EProperty
import io.verik.importer.ast.element.ERootPackage
import io.verik.importer.ast.element.ETypedElement

abstract class Visitor {

    open fun visitElement(element: EElement) {}

    open fun visitProject(compilationUnit: EProject) {
        visitElement(compilationUnit)
    }

    open fun visitTypedElement(typedElement: ETypedElement) {
        visitElement(typedElement)
    }

    open fun visitDeclaration(declaration: EDeclaration) {
        visitTypedElement(declaration)
    }

    open fun visitAbstractPackage(abstractPackage: EAbstractPackage) {
        visitElement(abstractPackage)
    }

    open fun visitRootPackage(rootPackage: ERootPackage) {
        visitAbstractPackage(rootPackage)
    }

    open fun visitModule(module: EModule) {
        visitDeclaration(module)
    }

    open fun visitAbstractProperty(abstractProperty: EAbstractProperty) {
        visitDeclaration(abstractProperty)
    }

    open fun visitProperty(property: EProperty) {
        visitAbstractProperty(property)
    }

    open fun visitPort(port: EPort) {
        visitAbstractProperty(port)
    }
}
