/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.common

fun <T> ArrayList<T>.replaceIfContains(old: T, new: T): Boolean {
    val index = indexOf(old)
    return if (index != -1) {
        set(index, new)
        true
    } else {
        false
    }
}
