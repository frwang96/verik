/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.target.serialize

import io.verik.compiler.target.common.Target

/**
 * Sequencer that creates the [TargetSerializationSequence]. This is the order that declarations will be serialized in
 * the Verik SystemVerilog package.
 */
object TargetSerializationSequencer {

    fun getSequence(): TargetSerializationSequence {
        val sequence = TargetSerializationSequence()
        sequence.add(
            Target.C_ArrayList,
            listOf(
                Target.ArrayList.F_new,
                Target.ArrayList.F_add,
                Target.ArrayList.F_get,
                Target.ArrayList.F_set,
                Target.ArrayList.F_size
            )
        )
        return sequence
    }
}
