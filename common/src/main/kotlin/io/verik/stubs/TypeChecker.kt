/*
 * Copyright 2020 Francis Wang
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

package io.verik.stubs

import io.verik.common.*
import io.verik.common.data.*

internal class TypeChecker {

    companion object {

        fun check(type: _instance, stub: StubEntry) {
            val stubName = stub.name
            val stubType = stub.instance
            val typeName = getTypeName(type)
            val stubTypeName = getTypeName(stubType)

            if (type is _uint) {
                if (stubType is _uint) {
                    if (type.SIZE != stubType.SIZE) {
                        throw IllegalArgumentException("size mismatch for $stubName expected ${type.SIZE} but was ${stubType.SIZE}")
                    }
                } else throw IllegalArgumentException("type mismatch for $stubName expected $typeName but was $stubTypeName")
            } else throw IllegalArgumentException("instance type $typeName not supported")
        }
    }
}
