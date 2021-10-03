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

import io.verik.compiler.ast.interfaces.ResizableElementContainer
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.replaceIfContains

abstract class EAbstractContainerClass : EAbstractClass(), ResizableElementContainer {

    abstract var members: ArrayList<EElement>

    override fun acceptChildren(visitor: TreeVisitor) {
        members.forEach { it.accept(visitor) }
    }

    override fun replaceChild(oldElement: EElement, newElement: EElement): Boolean {
        newElement.parent = this
        return members.replaceIfContains(oldElement, newElement)
    }

    override fun insertChild(element: EElement) {
        element.parent = this
        members.add(element)
    }
}
