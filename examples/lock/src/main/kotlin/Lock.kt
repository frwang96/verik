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

import verik.base.*
import verik.data.*

enum class State {
    OPENED,
    OPENING,
    CLOSED,
    CLOSING
}

@top object Top: Module() {

    @ins var lock = Lock(d(), d(), d(), d())
}

class Lock(
    @input var rst: Boolean,
    @input var clk: Boolean,
    @input var open: Boolean,
    @input var close: Boolean
): Module() {

    var state = State.CLOSED

    @seq fun update() {
        on (posedge(clk)) {
            if (rst) {
                state = State.CLOSED
            } else {
                when (state) {
                    State.OPENED -> if (close) state = State.CLOSING
                    State.OPENING -> state = State.OPENED
                    State.CLOSED -> if (open) state = State.OPENING
                    State.CLOSING -> state = State.CLOSED
                }
            }
        }
    }
}
