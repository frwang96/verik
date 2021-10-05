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

package io.verik.compiler.interpret

import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtEnumEntry
import io.verik.compiler.ast.element.sv.EEnum
import io.verik.compiler.ast.element.sv.ESvEnumEntry

object EnumInterpreter {

    fun interpretEnum(basicClass: EKtBasicClass, referenceUpdater: ReferenceUpdater): Boolean {
        if (!basicClass.isEnum)
            return false
        val entryReferences = basicClass.declarations
            .mapNotNull { it.cast<EKtEnumEntry>() }
            .map { interpretEnumEntry(it, referenceUpdater) }
        val enum = EEnum(
            basicClass.location,
            basicClass.name,
            entryReferences
        )
        referenceUpdater.replace(basicClass, enum)
        return true
    }

    private fun interpretEnumEntry(ktEnumEntry: EKtEnumEntry, referenceUpdater: ReferenceUpdater): ESvEnumEntry {
        val svEnumEntry = ESvEnumEntry(
            ktEnumEntry.location,
            ktEnumEntry.name,
            ktEnumEntry.type
        )
        referenceUpdater.update(ktEnumEntry, svEnumEntry)
        return svEnumEntry
    }
}
