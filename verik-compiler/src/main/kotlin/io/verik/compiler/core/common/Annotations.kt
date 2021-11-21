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

package io.verik.compiler.core.common

import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties

object Annotations {

    const val MAKE = "io.verik.core.Make"

    const val SYNTHESIS_TOP = "io.verik.core.SynthTop"
    const val SIMULATION_TOP = "io.verik.core.SimTop"

    const val IN = "io.verik.core.In"
    const val OUT = "io.verik.core.Out"

    const val COM = "io.verik.core.Com"
    const val SEQ = "io.verik.core.Seq"
    const val RUN = "io.verik.core.Run"
    const val TASK = "io.verik.core.Task"

    const val EXTERN = "io.verik.core.Extern"

    private val annotations = ArrayList<String>()

    init {
        annotations.add("kotlin.Suppress")
        Annotations::class.declaredMemberProperties.forEach {
            if (it.returnType == String::class.createType()) {
                val annotation = it.call() as String
                annotations.add(annotation)
            }
        }
    }

    fun isAnnotation(annotation: String): Boolean {
        return annotation in annotations
    }
}
