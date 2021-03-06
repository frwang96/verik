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

package count

import io.verik.core.*

@Top
object Count : Module() {

    var clk = false
    var rst = true
    var count = u<`8`>(0)

    @Seq
    fun update() {
        on (posedge(clk)) {
            println("count=$count")
            if (rst) count = u(0)
            else count += u<`8`>(1)
        }
    }

    @Run
    fun toggleClk() {
        clk = false
        forever {
            delay(1)
            clk = !clk
        }
    }

    @Run
    fun toggleRst() {
        rst = true
        delay(2)
        rst = false
        delay(16)
        finish()
    }
}