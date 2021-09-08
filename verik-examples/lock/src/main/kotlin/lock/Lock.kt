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

package lock

import io.verik.core.*

class Lock(
    @In var clk: Boolean,
    @In var rst: Boolean,
    @In var open: Boolean,
    @In var close: Boolean,
    @Out var state: State
) : Module() {

    @Seq
    fun updateState() {
        on(posedge(clk)) {
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
