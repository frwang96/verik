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

package io.verik.compiler.message

abstract class AbstractMessageTemplate {

    lateinit var name: String

    abstract val severity: Severity

    abstract val template: String

    internal fun format(vararg args: Any?): String {
        val builder = StringBuilder()
        var index = 0
        while (index < template.length) {
            val argIndex = getArgIndex(index)
            if (argIndex != null) {
                builder.append(args[argIndex])
                index++
            } else {
                builder.append(template[index])
            }
            index++
        }
        return builder.toString()
    }

    private fun getArgIndex(index: Int): Int? {
        return if (template[index] == '$') {
            val char = template.getOrNull(index + 1)
            if (char != null && char.isDigit()) {
                char - '0'
            } else null
        } else null
    }
}
