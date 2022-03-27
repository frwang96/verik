/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused", "UNUSED_PARAMETER")

package io.verik.core

/**
 * Prints the given [message] at the end of the current time step.
 */
fun strobe(message: String) {
    throw VerikException()
}

/**
 * Prints the given [message] at the end of each time step if its value has changed. Only one monitor call can be active
 * at any time.
 */
fun monitor(message: String) {
    throw VerikException()
}

/**
 * Exits the simulation with no error status.
 */
fun finish(): Nothing {
    throw VerikException()
}

/**
 * Exits the simulation with error status.
 */
fun fatal(): Nothing {
    throw VerikException()
}

/**
 * Logs [message] with severity fatal and exits the simulation with error status.
 */
fun fatal(message: String): Nothing {
    throw VerikException()
}

/**
 * Logs [message] with severity error.
 */
fun error(message: String) {
    throw VerikException()
}

/**
 * Logs [message] with severity warning.
 */
fun warning(message: String) {
    throw VerikException()
}

/**
 * Logs [message] with severity info.
 */
fun info(message: String) {
    throw VerikException()
}

/**
 * Returns the current simulation time.
 */
fun time(): Time {
    throw VerikException()
}
