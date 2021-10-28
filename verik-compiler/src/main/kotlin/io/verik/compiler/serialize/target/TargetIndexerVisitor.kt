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

package io.verik.compiler.serialize.target

import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.interfaces.Reference
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.target.common.TargetDeclaration

class TargetIndexerVisitor : TreeVisitor() {

    val targetDeclarationSet = HashSet<TargetDeclaration>()

    override fun visitTypedElement(typedElement: ETypedElement) {
        super.visitTypedElement(typedElement)
        addTargets(typedElement.type)
        if (typedElement is Reference) {
            val reference = typedElement.reference
            if (reference is TargetDeclaration)
                targetDeclarationSet.add(reference)
        }
    }

    private fun addTargets(type: Type) {
        type.arguments.forEach { addTargets(it) }
        val reference = type.reference
        if (reference is TargetDeclaration)
            targetDeclarationSet.add(reference)
    }
}
