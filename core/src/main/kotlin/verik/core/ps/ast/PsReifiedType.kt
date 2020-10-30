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

package verik.core.ps.ast

import verik.core.base.Symbol
import verik.core.rf.ast.RfReifiedType
import verik.core.rf.ast.RfTypeClass

enum class PsTypeClass {
    TYPE,
    INSTANCE;

    companion object {

        operator fun invoke(typeClass: RfTypeClass): PsTypeClass {
            return when (typeClass) {
                RfTypeClass.TYPE -> TYPE
                RfTypeClass.INSTANCE -> INSTANCE
            }
        }
    }
}

data class PsReifiedType(
        val type: Symbol,
        val typeClass: PsTypeClass,
        val args: List<Int>
) {

    override fun toString(): String {
        val argString = args.joinToString { it.toString() }
        return "$type($argString)"
    }

    constructor(reifiedType: RfReifiedType): this(
            reifiedType.type,
            PsTypeClass(reifiedType.typeClass),
            reifiedType.args
    )
}