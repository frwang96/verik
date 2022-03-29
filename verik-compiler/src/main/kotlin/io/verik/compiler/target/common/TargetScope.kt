/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.target.common

/**
 * A scope that contains target declarations. Target declarations defined in this scope belong to [parent].
 */
open class TargetScope(val parent: TargetDeclaration)
