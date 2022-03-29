/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.common

import io.verik.compiler.ast.property.AnnotationEntry
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties

object AnnotationEntries {

    val ENTRY = AnnotationEntry("io.verik.core.Entry")
    val INJ = AnnotationEntry("io.verik.core.Inj")
    val MAKE = AnnotationEntry("io.verik.core.Make")
    val IN = AnnotationEntry("io.verik.core.In")
    val OUT = AnnotationEntry("io.verik.core.Out")
    val WIRE = AnnotationEntry("io.verik.core.Wire")
    val RAND = AnnotationEntry("io.verik.core.Rand")
    val RANDC = AnnotationEntry("io.verik.core.Randc")
    val CONS = AnnotationEntry("io.verik.core.Cons")
    val COVER = AnnotationEntry("io.verik.core.Cover")
    val COM = AnnotationEntry("io.verik.core.Com")
    val SEQ = AnnotationEntry("io.verik.core.Seq")
    val RUN = AnnotationEntry("io.verik.core.Run")
    val TASK = AnnotationEntry("io.verik.core.Task")

    private val annotationEntries = ArrayList<AnnotationEntry>()

    init {
        annotationEntries.add(AnnotationEntry("kotlin.Suppress"))
        AnnotationEntries::class.declaredMemberProperties.forEach {
            if (it.returnType == AnnotationEntry::class.createType()) {
                val annotationEntry = it.get(AnnotationEntries) as AnnotationEntry
                annotationEntries.add(annotationEntry)
            }
        }
    }

    fun isAnnotationEntry(annotationEntry: AnnotationEntry): Boolean {
        return annotationEntry in annotationEntries
    }
}
