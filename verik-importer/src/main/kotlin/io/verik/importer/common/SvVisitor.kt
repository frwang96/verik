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

import io.verik.importer.ast.sv.element.SvCompilationUnit
import io.verik.importer.ast.sv.element.SvDeclaration
import io.verik.importer.ast.sv.element.SvElement
import io.verik.importer.ast.sv.element.SvModule
import io.verik.importer.ast.sv.element.SvPackage
import io.verik.importer.ast.sv.element.SvPort
import io.verik.importer.ast.sv.element.SvProperty

abstract class SvVisitor {

    open fun visitElement(element: SvElement) {}

    open fun visitCompilationUnit(compilationUnit: SvCompilationUnit) {
        visitElement(compilationUnit)
    }

    open fun visitDeclaration(declaration: SvDeclaration) {
        visitElement(declaration)
    }

    open fun visitPackage(`package`: SvPackage) {
        visitDeclaration(`package`)
    }

    open fun visitModule(module: SvModule) {
        visitDeclaration(module)
    }

    open fun visitProperty(property: SvProperty) {
        visitDeclaration(property)
    }

    open fun visitPort(port: SvPort) {
        visitDeclaration(port)
    }
}
