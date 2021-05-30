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

abstract class Visitor<R> {

    open fun visitFile(file: VkFile): R? {
        return visitElement(file)
    }

    open fun visitImportDirective(importDirective: VkImportDirective): R? {
        return visitElement(importDirective)
    }

    open fun visitModule(module: VkModule): R? {
        return visitBaseClass(module)
    }

    open fun visitBaseClass(baseClass: VkBaseClass): R? {
        return visitDeclaration(baseClass)
    }

    open fun visitBaseFunction(baseFunction: VkBaseFunction): R? {
        return visitDeclaration(baseFunction)
    }

    open fun visitBaseProperty(baseProperty: VkBaseProperty): R? {
        return visitDeclaration(baseProperty)
    }

    open fun visitDeclaration(declaration: VkDeclaration): R? {
        return visitElement(declaration)
    }

    open fun visitElement(element: VkElement): R? {
        return null
    }
}