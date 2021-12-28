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

package io.verik.compiler.cast

import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.psi.KtAnnotationEntry

object AnnotationEntryCaster {

    fun castAnnotationEntry(annotationEntry: KtAnnotationEntry, castContext: CastContext): AnnotationEntry {
        val descriptor = castContext.sliceAnnotation[annotationEntry]!!
        val qualifiedName = descriptor.fqName!!.asString()
        val castedAnnotationEntry = AnnotationEntry(qualifiedName)
        if (!AnnotationEntries.isAnnotationEntry(castedAnnotationEntry))
            Messages.UNSUPPORTED_ANNOTATION.on(annotationEntry, castedAnnotationEntry)
        return castedAnnotationEntry
    }
}