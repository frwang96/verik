/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.target.common

interface CompositeTarget {

    val name: String
    val contentProlog: String
    val contentBody: String
    val contentEpilog: String
}
