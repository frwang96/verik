/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.core

/**
 * A core declaration that represents a cardinal function.
 */
class CoreCardinalFunctionDeclaration(
    override val name: String
) : CoreDeclaration, CardinalDeclaration
