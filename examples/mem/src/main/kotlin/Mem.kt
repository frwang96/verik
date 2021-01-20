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

    @input  var clk      = t_Boolean()
    @input  var rst      = t_Boolean()
    @input  var write_en = t_Boolean()
    @input  var addr     = t_Ubit(ADDR_WIDTH)
    @input  var data_in  = t_Ubit(DATA_WIDTH)
    @output var data_out = t_Ubit(DATA_WIDTH)

    private var mem = t_Array(64, t_Ubit(DATA_WIDTH))

    @seq fun update() {
        on (posedge(clk)) {
            if (rst) {
                for (i in range(256)) {
                    mem[i] = u(0)
                }
            } else {
                if (write_en) {
                    mem[addr] = data_in
                } else {
                    data_out = mem[addr]
                }
            }
        }
    }
}