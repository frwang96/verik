/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.main

/**
 * Exception to be thrown when the compiler terminates with an error status. This should be thrown by the message
 * collector and should not be thrown directly.
 */
class VerikCompilerException : Exception()
