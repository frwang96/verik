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

@top class _mem_tb: _module() {

    private var clk      = _bool()
    private var write_en = _bool()
    private var addr     = _ubit(6)
    private var data_in  = _ubit(8)
    private var data_out = _ubit(8)

    @make private var mem = _mem() with {
        it.clk      = clk
        it.write_en = write_en
        it.addr     = addr
        it.data_in  = data_in
        data_out    = it.data_out
    }

    @run fun toggle_clk() {
        clk = false
        forever {
            delay(1)
            clk = !clk
        }
    }
}