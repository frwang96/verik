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

import verik.data.*

internal class _rconf_formatter {

    companion object {

        fun get_string(rconf: Any): String {
            val type_name = rconf::class.simpleName
            return if (rconf is _ubit) {
                rconf.toString()
            } else throw IllegalArgumentException("type $type_name not supported")
        }

        fun get_encoding(rconf: Any): String {
            val type_name = rconf::class.simpleName
            return if (rconf is _ubit) {
                rconf.toString()
            } else throw IllegalArgumentException("type $type_name not supported")
        }
    }
}