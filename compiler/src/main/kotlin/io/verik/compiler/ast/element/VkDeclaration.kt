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

import io.verik.compiler.ast.common.Name
import io.verik.compiler.ast.common.QualifiedName
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.descriptor.DeclarationDescriptor
import io.verik.compiler.main.messageCollector

abstract class VkDeclaration: VkElement() {

    abstract var name: Name
    abstract var qualifiedName: QualifiedName
    abstract var type: Type

    override fun toString(): String {
        return "$name"
    }

    override fun copy(): VkDeclaration {
        messageCollector.fatal("Declaration should be copied through child class", this)
    }

    abstract fun getDescriptor(): DeclarationDescriptor
}