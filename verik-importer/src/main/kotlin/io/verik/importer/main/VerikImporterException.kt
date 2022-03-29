/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.main

/**
 * Exception to be thrown when the importer terminates with an error status. This should be thrown by the message
 * collector and should not be thrown directly.
 */
class VerikImporterException : Exception()
