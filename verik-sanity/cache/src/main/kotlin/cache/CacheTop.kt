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

@SimTop
object CacheTop : Module() {

    var clk = false

    @Make
    val txnIf_tb_cache = TxnIf(clk)

    @Make
    val txnIf_cache_mainMem = TxnIf(clk)

    @Make
    val cache = Cache(clk, txnIf_tb_cache.rx, txnIf_cache_mainMem.tx)

    @Make
    val mainMem = MainMem(clk, txnIf_cache_mainMem.rx)

    @Make
    val tb = CacheTb(txnIf_tb_cache.tb)

    @Run
    fun toggleClk() {
        forever {
            delay(1)
            clk = !clk
        }
    }
}
