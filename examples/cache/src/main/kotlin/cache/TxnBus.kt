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

class TxnBus : Bus() {

    var rst: Boolean = x()
    var reqOp: Op = x()
    var reqAddr: UbitAddr = x()
    var reqData: UbitData = x()
    var rspVld: Boolean = x()
    var rspData: UbitData = x()

    @Make
    val txp = TxnTxPort(
        rst = rst,
        reqOp = reqOp,
        reqAddr = reqAddr,
        reqData = reqData,
        rspVld = rspVld,
        rspData = rspData
    )

    @Make
    val rxp = TxnRxPort(
        rst = rst,
        reqOp = reqOp,
        reqAddr = reqAddr,
        reqData = reqData,
        rspVld = rspVld,
        rspData = rspData
    )

    class TxnTxPort(
        @Out var rst: Boolean,
        @Out var reqOp: Op,
        @Out var reqAddr: UbitAddr,
        @Out var reqData: UbitData,
        @In  var rspVld: Boolean,
        @In  var rspData: UbitData
    ) : Port()

    class TxnRxPort(
        @In  var rst: Boolean,
        @In  var reqOp: Op,
        @In  var reqAddr: UbitAddr,
        @In  var reqData: UbitData,
        @Out var rspVld: Boolean,
        @Out var rspData: UbitData
    ) : Port()
}