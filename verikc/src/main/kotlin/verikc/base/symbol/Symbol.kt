/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.base.symbol

import verikc.base.ast.TypeClass.INSTANCE
import verikc.base.ast.TypeClass.TYPE
import verikc.base.ast.TypeGenerified
import verikc.base.ast.TypeGenerifiedSimple

data class Symbol(
    val index: Int
) {

    override fun toString(): String {
        return "[[$index]]"
    }

    fun toTypeGenerifiedInstance(vararg args: Int): TypeGenerified {
        return TypeGenerified(this, INSTANCE, args.toList())
    }

    fun toTypeGenerifiedType(vararg args: Int): TypeGenerified {
        return TypeGenerified(this, TYPE, args.toList())
    }

    fun toTypeGenerified(vararg args: Int): TypeGenerifiedSimple {
        return TypeGenerifiedSimple(this, args.toList())
    }

    companion object {

        val NULL = Symbol(0)
    }
}
