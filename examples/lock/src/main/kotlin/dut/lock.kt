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

package dut

import verik.common.*
import verik.common.data.*

enum class _state(override val value: _int = enum_sequential()): _enum {
    OPENED,
    OPENING,
    CLOSED,
    CLOSING
}

@top class _lock: _module {
    @input  var reset = _bool()
    @input  var clk   = _bool()
    @input  var open  = _bool()
    @output var close = _bool()

    var state = _state()

    @seq fun update() {
        on (posedge(clk)) {
            if (reset) {
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
