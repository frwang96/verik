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

package io.verik.compiler.ast.common

import io.verik.compiler.ast.element.*

abstract class Visitor<T> {

    open fun visitFile(file: VkFile): T? {
        return visitElement(file)
    }

    open fun visitImportDirective(importDirective: VkImportDirective): T? {
        return visitElement(importDirective)
    }

    open fun visitModule(module: VkModule): T? {
        return visitBaseClass(module)
    }

    open fun visitBaseClass(baseClass: VkBaseClass): T? {
        return visitDeclaration(baseClass)
    }

    open fun visitBaseFunction(baseFunction: VkBaseFunction): T? {
        return visitDeclaration(baseFunction)
    }

    open fun visitBaseProperty(baseProperty: VkBaseProperty): T? {
        return visitDeclaration(baseProperty)
    }

    open fun visitDeclaration(declaration: VkDeclaration): T? {
        return visitElement(declaration)
    }

    open fun visitElement(element: VkElement): T? {
        return null
    }
}