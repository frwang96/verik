/*
 * Copyright (c) 2020 Francis Wang
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

package verik.rconf

import verik.data.*

internal class TypeChecker {

    companion object {

        fun check(type: Data, entry: RconfEntry) {
            val type_name = type::class.simpleName
            val name = entry.name
            val data = entry.data
            val data_type_name = data::class.simpleName

            if (type is Ubit) {
                if (data is Ubit) {
                    if (type.WIDTH != data.WIDTH) {
                        throw IllegalArgumentException(
                            "width mismatch for $name expected ${type.WIDTH} but was ${data.WIDTH}")
                    }
                } else throw IllegalArgumentException(
                    "type mismatch for $name expected $type_name but was $data_type_name")
            } else throw IllegalArgumentException("type $type_name not supported")
        }
    }
}
