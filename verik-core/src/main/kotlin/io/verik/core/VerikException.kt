/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.core

import kotlin.reflect.KProperty

internal class VerikException : Exception(
    "Verik declaration should not be run as Kotlin"
)

internal class VerikExceptionDelegate<T> {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        throw VerikException()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        throw VerikException()
    }
}
