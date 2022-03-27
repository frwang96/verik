/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.message

abstract class AbstractMessageTemplate {

    lateinit var name: String

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
