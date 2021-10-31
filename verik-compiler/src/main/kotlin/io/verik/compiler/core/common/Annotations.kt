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

object Annotations {

    const val SYNTHESIS_TOP = "io.verik.core.SynthTop"
    const val SIMULATION_TOP = "io.verik.core.SimTop"
    const val RELABEL = "io.verik.core.Relabel"

    const val COM = "io.verik.core.Com"
    const val SEQ = "io.verik.core.Seq"
    const val RUN = "io.verik.core.Run"
    const val TASK = "io.verik.core.Task"

    const val MAKE = "io.verik.core.Make"

    const val IN = "io.verik.core.In"
    const val OUT = "io.verik.core.Out"
}
