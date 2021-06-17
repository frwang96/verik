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

import io.verik.compiler.ast.common.*
import io.verik.compiler.main.MessageLocation

open class VkBaseFunction(
    override val location: MessageLocation,
    override var name: Name,
    override var type: Type,
    var annotationType: FunctionAnnotationType?,
    var bodyBlockExpression: VkBlockExpression?
) : VkDeclaration() {

    override fun accept(visitor: Visitor) {
        return visitor.visitBaseFunction(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {}
}