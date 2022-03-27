/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.target.serialize

import io.verik.compiler.target.common.CompositeTarget
import io.verik.compiler.target.common.CompositeTargetClassDeclaration
import io.verik.compiler.target.common.CompositeTargetFunctionDeclaration

sealed class TargetSerializationEntry {

    abstract fun getCompositeTargets(): List<CompositeTarget>
}

data class FunctionTargetSerializationEntry(
    val functionDeclaration: CompositeTargetFunctionDeclaration
) : TargetSerializationEntry() {

    override fun getCompositeTargets(): List<CompositeTarget> {
        return listOf(functionDeclaration)
    }
}

data class ClassTargetSerializationEntry(
    val classDeclaration: CompositeTargetClassDeclaration,
    val functionDeclarations: List<CompositeTargetFunctionDeclaration>
) : TargetSerializationEntry() {

    override fun getCompositeTargets(): List<CompositeTarget> {
        return listOf(classDeclaration) + functionDeclarations
    }
}
