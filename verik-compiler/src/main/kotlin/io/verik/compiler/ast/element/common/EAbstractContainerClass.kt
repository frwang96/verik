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

package io.verik.compiler.ast.element.common

import io.verik.compiler.ast.common.ResizableDeclarationContainer
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.replaceIfContains
import io.verik.compiler.message.Messages

abstract class EAbstractContainerClass : EAbstractClass(), ResizableDeclarationContainer {

    abstract var declarations: ArrayList<EDeclaration>

    override fun acceptChildren(visitor: TreeVisitor) {
        declarations.forEach { it.accept(visitor) }
    }

    override fun replaceChild(oldDeclaration: EDeclaration, newDeclaration: EDeclaration): Boolean {
        newDeclaration.parent = this
        return declarations.replaceIfContains(oldDeclaration, newDeclaration)
    }

    override fun insertChild(declaration: EDeclaration) {
        declaration.parent = this
        declarations.add(declaration)
    }

    override fun insertChildBefore(oldDeclaration: EDeclaration, newDeclaration: EDeclaration) {
        newDeclaration.parent = this
        val index = declarations.indexOf(oldDeclaration)
        if (index != -1) {
            declarations.add(index, newDeclaration)
        } else {
            Messages.INTERNAL_ERROR.on(oldDeclaration, "Could not find declaration: ${oldDeclaration.name}")
        }
    }
}
