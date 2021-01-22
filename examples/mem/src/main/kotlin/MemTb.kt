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

class MemTb: Module() {

    private var clk      = t_Boolean()
    private var rst      = t_Boolean()
    private var write_en = t_Boolean()
    private var addr     = t_Ubit(ADDR_WIDTH)
    private var data_in  = t_Ubit(DATA_WIDTH)
    private var data_out = t_Ubit(DATA_WIDTH)

    @make val mem = t_Mem().with(
        clk      = clk,
        rst      = rst,
        write_en = write_en,
        addr     = addr,
        data_in  = data_in,
        data_out = data_out
    )

    @run fun toggle_clk() {
        clk = false
        forever {
            delay(1)
            clk = !clk
        }
    }

    @run fun run_test() {
        write_mem()
        finish()
    }

    @task fun write_mem() {
        wait(negedge(clk))
        write_en = true
        for (i in range(exp(ADDR_WIDTH))) {
            data_in = u(i)
            wait(negedge(clk))
        }
        write_en = false
    }
}