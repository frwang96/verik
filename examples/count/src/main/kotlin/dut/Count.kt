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

class Count: Module() {

    var clk   = t_Boolean()
    var rst   = t_Boolean()
    var count = t_Ubit(8)

    @seq fun update() {
        on (posedge(clk)) {
            println("count=$count")
            if (rst) count = u(0)
            else count += u(1)
        }
    }

    @run fun toggle_clk() {
        clk = false
        forever {
            delay(1)
            clk = !clk
        }
    }

    @run fun toggle_rst() {
        rst = true
        delay(2)
        rst = false
        delay(16)
        finish()
    }
}