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

package dut

import verik.base.*
import verik.data.*

enum class _state(val value: _ubit = enum_sequential()) {
    OPENED,
    OPENING,
    CLOSED,
    CLOSING
}

@top class _lock: _module() {
    @input var rst = _bool()
    @input var clk   = _bool()
    @input var open  = _bool()
    @input var close = _bool()

    private var state = _state()

    @seq fun update() {
        on (posedge(clk)) {
            if (rst) {
                state = _state.CLOSED
            } else {
                when (state) {
                    _state.OPENED -> if (close) state = _state.CLOSING
                    _state.OPENING -> state = _state.OPENED
                    _state.CLOSED -> if (open) state = _state.OPENING
                    _state.CLOSING -> state = _state.CLOSED
                }
            }
        }
    }
}
