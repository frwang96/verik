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

class _test: _module() {

    @bus val arb_bus = _arb_bus()

    @run fun test() {
        wait(posedge(arb_bus.clk))
        arb_bus.request = ubit(0b01)
        println("@${time()}: Drove req")
        repeat(2) { wait(posedge(arb_bus.clk)) }
        if (arb_bus.grant == ubit(0b01)) {
            println("@${time()}: Success")
        } else {
            println("@${time()}: Error")
        }
    }
}