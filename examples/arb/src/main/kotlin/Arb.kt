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

class Arb: Module() {

    @inout val arb_bp = ArbDutBusPort()

    @seq fun update() {
        on (posedge(arb_bp.clk), posedge(arb_bp.rst)) {
            arb_bp.grant = when {
                arb_bp.rst -> u(0)
                arb_bp.request[0] -> u(0b01)
                arb_bp.request[1] -> u(0b10)
                else -> u(0)
            }
        }
    }
}