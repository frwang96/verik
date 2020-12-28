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

internal class _rconf_formatter {

    companion object {

        fun get_string(data: _data): String {
            val type_name = data::class.simpleName
            return when (data) {
                is _ubit -> "0x" + _bit_util.get_hex_string(data.SIZE, data.value)
                is _sbit -> "0x" + _bit_util.get_hex_string(data.SIZE, data.value)
                else -> throw IllegalArgumentException("type $type_name not supported")
            }
        }

        fun get_encoding(data: _data): String {
            val type_name = data::class.simpleName
            return when (data) {
                is _ubit -> _bit_util.get_hex_string(data.SIZE, data.value)
                is _sbit -> _bit_util.get_hex_string(data.SIZE, data.value)
                else -> throw IllegalArgumentException("type $type_name not supported")
            }
        }
    }
}