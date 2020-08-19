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

package verik.core.sv

import verik.core.base.Line

enum class SvActionBlockType {
    ALWAYS_COMB,
    ALWAYS_FF,
    INITIAL
}

data class SvActionBlock(
        override val line: Int,
        val type: SvActionBlockType,
        val block: SvBlock
): Line, SvBuildable {

    override fun build(builder: SvSourceBuilder) {
        when (type) {
            SvActionBlockType.ALWAYS_COMB -> {
                builder.append("always_comb ")
            }
            SvActionBlockType.ALWAYS_FF -> {
                builder.append("always_ff ")
            }
            SvActionBlockType.INITIAL -> {
                builder.append("initial ")
            }
        }
        block.build(builder)
    }
}