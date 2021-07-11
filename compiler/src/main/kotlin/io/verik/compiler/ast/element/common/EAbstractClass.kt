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

import io.verik.compiler.ast.interfaces.DeclarationContainer
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.replaceIfContains
import io.verik.compiler.main.m

abstract class EAbstractClass : EDeclaration(), DeclarationContainer {

    abstract var supertype: Type
    abstract var typeParameters: ArrayList<ETypeParameter>
    abstract var declarations: ArrayList<EDeclaration>

    override fun acceptChildren(visitor: TreeVisitor) {
        declarations.forEach { it.accept(visitor) }
    }

    override fun replaceChild(oldDeclaration: EDeclaration, newDeclaration: EDeclaration) {
        newDeclaration.parent = this
        if (!declarations.replaceIfContains(oldDeclaration, newDeclaration))
            m.error("Could not find declaration $oldDeclaration in ${this::class.simpleName}", this)
    }
}