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

import io.verik.common.data.*

internal class DataFormatter {

    companion object {

        fun getString(data: _data): String {
            val typeName = getTypeName(data)
            return if (data is _uint) {
                data.toString()
            } else throw IllegalArgumentException("data type $typeName not supported")
        }

        fun getEncoding(data: _data): String {
            val typeName = getTypeName(data)
            return if (data is _uint) {
                getHexString(data.SIZE, data.bits)
            } else throw IllegalArgumentException("data type $typeName not supported")
        }
    }
}