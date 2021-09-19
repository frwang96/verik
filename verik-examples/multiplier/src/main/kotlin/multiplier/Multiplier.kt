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

package multiplier

import io.verik.core.*

class Multiplier(
    @In var clk: Boolean,
    @In var rst: Boolean,
    @In var req: MultiplierReq,
    @Out var rsp: MultiplierRsp
) : Module() {

    var a: Ubit<`8`> = nc()
    var b: Ubit<REQ_WIDTH> = nc()
    var prod: Ubit<REQ_WIDTH> = nc()
    var tp: Ubit<REQ_WIDTH> = nc()
    var i: Ubit<INCLOG<REQ_WIDTH>> = nc()

    @Com
    fun setReq() {
        rsp.vld = (i == u<REQ_WIDTH>())
        rsp.result = cat(tp, prod)
    }
}
