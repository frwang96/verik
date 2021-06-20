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

package io.verik.compiler.ast.element

import io.verik.compiler.ast.common.Type
import io.verik.compiler.main.m

abstract class VkBaseClass : VkDeclaration() {

    abstract var supertype: Type
    abstract var typeParameters: ArrayList<VkTypeParameter>
    abstract var declarations: ArrayList<VkDeclaration>

    fun replace(baseClass: VkBaseClass) {
        parent.cast<VkFile>(this)?.replaceChild(this, baseClass)
    }

    fun removeChild(declaration: VkDeclaration) {
        if (!declarations.remove(declaration))
            m.error("Could not find declaration $declaration", declaration)
    }

    fun insertSibling(declaration: VkDeclaration) {
        parent.cast<VkFile>(this)?.insertChild(this, declaration)
    }
}