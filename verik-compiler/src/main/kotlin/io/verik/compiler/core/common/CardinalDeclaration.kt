/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.common

import io.verik.compiler.ast.common.Declaration

/**
 * The base class of all cardinal declarations. A cardinal corresponds to an integer. It is used as a type argument to
 * represent for instance the width of bit types or the size of packed and unpacked arrays.
 */
interface CardinalDeclaration : Declaration
