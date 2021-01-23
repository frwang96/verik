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
import verik.collection.*
import verik.data.*

val ADDR_WIDTH = 6
val DATA_WIDTH = 8

class Mem: Module() {

    @inout val bp = t_MemRxBusPort()

    private var mem = t_Array(exp(ADDR_WIDTH), t_Ubit(DATA_WIDTH))

    @seq fun update() {
        on (posedge(bp.clk)) {
            if (bp.rst) {
                for (i in range(exp(ADDR_WIDTH))) {
                    mem[i] = u(0)
                }
            } else {
                if (bp.write_en) {
                    mem[bp.addr] = bp.data_in
                } else {
                    bp.data_out = mem[bp.addr]
                }
            }
        }
    }
}