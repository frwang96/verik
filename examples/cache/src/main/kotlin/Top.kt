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

val ADDR_WIDTH = 6
val DATA_WIDTH = 8

val INDEX_WIDTH = 3
val TAG_WIDTH = 3

class Top: Module() {

    private var clk = t_Boolean()

    @make val cache_bus = t_MemBus().with(clk)

    @make val mem_bus = t_MemBus().with(clk)

    @make val cache = t_Cache().with(clk, cache_bus.rx_bp, mem_bus.tx_bp)

    @make val mem = t_Mem().with(clk, mem_bus.rx_bp)

    @make val tb = t_CacheTb().with(cache_bus.tb_bp)

    @run fun toggle_clk() {
        clk = false
        forever {
            delay(1)
            clk = !clk
        }
    }
}