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

package cache

import io.verik.core.*

@Top
object CacheTop : Module() {

    var clk = false

    @Make
    val txnBus_tb_cache = TxnBus()

    @Make
    val txnBus_cache_mainMem = TxnBus()

    @Make
    val cache = Cache(clk, txnBus_tb_cache.rxp, txnBus_cache_mainMem.txp)

    @Make
    val mainMem = MainMem(clk, txnBus_cache_mainMem.rxp)

    @Make
    val tb = CacheTb(clk, txnBus_tb_cache.txp)

    @Run
    fun toggleClk() {
        forever {
            delay(1)
            clk = !clk
        }
    }
}