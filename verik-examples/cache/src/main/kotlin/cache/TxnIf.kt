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

class TxnIf(
    @In var clk: Boolean
) : ModuleInterface() {

    var rst: Boolean = nc()
    var reqOp: Op = nc()
    var reqAddr: UbitAddr = nc()
    var reqData: UbitData = nc()
    var rspVld: Boolean = nc()
    var rspData: UbitData = nc()

    @Make
    val tx = TxnTx(
        rst = rst,
        reqOp = reqOp,
        reqAddr = reqAddr,
        reqData = reqData,
        rspVld = rspVld,
        rspData = rspData
    )

    @Make
    val rx = TxnRx(
        rst = rst,
        reqOp = reqOp,
        reqAddr = reqAddr,
        reqData = reqData,
        rspVld = rspVld,
        rspData = rspData
    )

    @Make
    val cb = TxnCb(
        event = posedge(clk),
        rst = rst,
        reqOp = reqOp,
        reqAddr = reqAddr,
        reqData = reqData,
        rspVld = rspVld,
        rspData = rspData
    )

    @Make
    val tb = TxnTb(cb)

    class TxnTx(
        @Out var rst: Boolean,
        @Out var reqOp: Op,
        @Out var reqAddr: UbitAddr,
        @Out var reqData: UbitData,
        @In var rspVld: Boolean,
        @In var rspData: UbitData
    ) : ModulePort()

    class TxnRx(
        @In var rst: Boolean,
        @In var reqOp: Op,
        @In var reqAddr: UbitAddr,
        @In var reqData: UbitData,
        @Out var rspVld: Boolean,
        @Out var rspData: UbitData
    ) : ModulePort()

    class TxnCb(
        override val event: Event,
        @Out var rst: Boolean,
        @Out var reqOp: Op,
        @Out var reqAddr: UbitAddr,
        @Out var reqData: UbitData,
        @In var rspVld: Boolean,
        @In var rspData: UbitData
    ) : ClockingBlock()

    class TxnTb(
        val cb: TxnCb
    ) : ModulePort()
}
