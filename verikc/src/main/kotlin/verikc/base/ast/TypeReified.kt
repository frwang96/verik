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

package verikc.base.ast

enum class TypeClass {
    TYPE,
    INSTANCE
}

data class TypeReified(
    val typeSymbol: Symbol,
    val typeClass: TypeClass,
    val args: List<Int>
) {

    fun toInstance(line: Line): TypeReified {
        if (typeClass != TypeClass.TYPE) {
            throw LineException("type expression expected", line)
        }
        return TypeReified(
                typeSymbol,
                TypeClass.INSTANCE,
                args
        )
    }

    override fun toString(): String {
        val argString = args.joinToString { it.toString() }
        return "$typeSymbol($argString)"
    }
}
