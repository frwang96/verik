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

package io.verik.common.data

import io.verik.common.*

// infix fun _enum.put(x: _enum?) {}
// infix fun _enum.reg(x: _enum?) {}
// infix fun _enum.drive(x: _enum?) {}
// infix fun _enum.con(x: _enum?) {}
interface _enum: _data {

    val value: _uint

    companion object {

        fun sequential(): _uint {
            throw VerikDslException("function")
        }

        fun one_hot(): _uint {
            throw VerikDslException("function")
        }

        fun zero_one_hot(): _uint {
            throw VerikDslException("function")
        }
    }
}
