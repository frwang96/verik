/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.target.serialize

import io.verik.compiler.target.common.CompositeTargetClassDeclaration
import io.verik.compiler.target.common.CompositeTargetFunctionDeclaration

class TargetSerializationSequence {

    val entries = ArrayList<TargetSerializationEntry>()

    fun add(functionDeclaration: CompositeTargetFunctionDeclaration) {
        entries.add(FunctionTargetSerializationEntry(functionDeclaration))
    }

    fun add(
        classDeclaration: CompositeTargetClassDeclaration,
        functionDeclarations: List<CompositeTargetFunctionDeclaration>
    ) {
        entries.add(ClassTargetSerializationEntry(classDeclaration, functionDeclarations))
    }
}
