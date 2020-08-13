/*
 * Copyright 2020 Francis Wang
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

package io.verik.core.svx

import io.verik.core.main.Line
import io.verik.core.main.SourceBuilder

enum class SvxActionBlockType {
    ALWAYS_COMB,
    ALWAYS_FF,
    INITIAL
}

data class SvxActionBlock(
        override val line: Int,
        val type: SvxActionBlockType
): Line {

    fun build(builder: SourceBuilder) {
        builder.label(this)
        when (type) {
            SvxActionBlockType.ALWAYS_COMB -> {
                builder.appendln("always_comb begin")
            }
            SvxActionBlockType.ALWAYS_FF -> {
                builder.appendln("always_ff begin")
            }
            SvxActionBlockType.INITIAL -> {
                builder.appendln("initial begin")
            }
        }
        builder.appendln("end")
    }
}