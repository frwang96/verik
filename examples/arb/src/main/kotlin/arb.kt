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

class _arb: _module() {

    @bus val arb_bus = _arb_bus()

    @seq fun update() {
        on (posedge(arb_bus.clk), posedge(arb_bus.rst)) {
            arb_bus.grant = when {
                arb_bus.rst -> ubit(0)
                arb_bus.request[0] -> ubit(0b01)
                arb_bus.request[1] -> ubit(0b10)
                else -> ubit(0)
            }
        }
    }
}