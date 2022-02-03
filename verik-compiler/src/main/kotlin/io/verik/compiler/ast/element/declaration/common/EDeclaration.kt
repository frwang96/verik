/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.compiler.ast.element.declaration.common

import io.verik.compiler.ast.common.Declaration
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.property.AnnotationEntry

abstract class EDeclaration : ETypedElement(), Declaration {

    abstract var annotationEntries: List<AnnotationEntry>
    abstract var documentationLines: List<String>?

    fun hasAnnotationEntry(annotationEntry: AnnotationEntry): Boolean {
        return annotationEntries.any { it == annotationEntry }
    }

    fun isSpecializable(): Boolean {
        return when (val parent = this.parent) {
            is EFile -> true
            is EKtClass -> this in parent.declarations || this == parent.primaryConstructor
            else -> false
        }
    }
}
