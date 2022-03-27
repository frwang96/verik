/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.test

class TestErrorException(override val message: String) : Exception(message)

class TestWarningException(override val message: String) : Exception(message)
