/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.verik.compiler.serialize.target

import io.verik.compiler.target.common.CompositeTargetClassDeclaration
import io.verik.compiler.target.common.CompositeTargetFunctionDeclaration
import io.verik.compiler.target.common.Target

object TargetSerializationSequencer {

    fun getEntries(): List<TargetSerializationEntry> {
        val sequence = TargetSerializationSequence()
        sequence.add(
            Target.C_ArrayList,
            Target.ArrayList.F_new,
            Target.ArrayList.F_add
        )
        return sequence.entries
    }

    private class TargetSerializationSequence {

        val entries = ArrayList<TargetSerializationEntry>()

        fun add(
            targetClassDeclaration: CompositeTargetClassDeclaration,
            vararg targetFunctionDeclarations: CompositeTargetFunctionDeclaration
        ) {
            entries.add(TargetSerializationEntry(targetClassDeclaration, targetFunctionDeclarations.asList()))
        }
    }
}
