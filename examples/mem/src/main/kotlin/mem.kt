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

class _mem: _module() {

    @input  var clk      = _bool()
    @input  var write_en = _bool()
    @input  var addr     = _ubit(6)
    @input  var data_in  = _ubit(8)
    @output var data_out = _ubit(8)

    private var mem = _array(256, _ubit(8))

    @seq fun update() {
        on (posedge(clk)) {
            if (write_en) {
                mem[addr] = data_in
            } else {
                data_out = mem[addr]
            }
        }
    }
}