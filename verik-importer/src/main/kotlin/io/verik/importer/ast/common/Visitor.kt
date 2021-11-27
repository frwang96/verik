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

package io.verik.importer.ast.common

import io.verik.importer.ast.element.ECompilationUnit
import io.verik.importer.ast.element.EDeclaration
import io.verik.importer.ast.element.EElement
import io.verik.importer.ast.element.EModule

abstract class Visitor {

    open fun visitElement(element: EElement) {}

    open fun visitCompilationUnit(compilationUnit: ECompilationUnit) {
        visitElement(compilationUnit)
    }

//  DECLARATION  ///////////////////////////////////////////////////////////////////////////////////////////////////////

    open fun visitDeclaration(declaration: EDeclaration) {
        visitElement(declaration)
    }

    open fun visitModule(module: EModule) {
        visitDeclaration(module)
    }
}
