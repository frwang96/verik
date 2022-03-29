/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.cast

import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.psi.KtAnnotationEntry

/**
 * Caster that builds [annotation entries][AnnotationEntry].
 */
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
