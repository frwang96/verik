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

@top object Top: Module() {

    var clk = false

    @ins val cache_bus = MemBus(clk)

    @ins val mem_bus = MemBus(clk)

    @ins val cache = Cache(clk, cache_bus.rx_bp, mem_bus.tx_bp)

    @ins val mem = Mem(clk, mem_bus.rx_bp)

    @ins val tb = CacheTb(cache_bus.tb_bp)

    @run fun toggle_clk() {
        forever {
            delay(1)
            clk = !clk
        }
    }
}