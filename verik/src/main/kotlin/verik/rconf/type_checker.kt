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

package verik.rconf

import verik.base.*
import verik.data.*

internal class _type_checker {

    companion object {

        fun check(reference: _any, entry: _rconf_entry) {
            val name = entry.name
            val value = entry.value
            val value_type = value::class.simpleName
            val reference_type = reference::class.simpleName

            if (reference is _ubit) {
                if (value is _ubit) {
                    if (reference.SIZE != value.SIZE) {
                        throw IllegalArgumentException("size mismatch for $name expected ${reference.SIZE} but was ${value.SIZE}")
                    }
                } else throw IllegalArgumentException("type mismatch for $name expected $reference_type but was $value_type")
            } else throw IllegalArgumentException("type $reference_type not supported")
        }
    }
}
