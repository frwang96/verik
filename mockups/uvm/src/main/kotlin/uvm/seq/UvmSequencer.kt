/*
 * Copyright (c) 2020 Francis Wang
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

@file:Suppress("UNUSED_PARAMETER", "unused")

package uvm.seq

import uvm.tlm1.UvmSeqItemPullImp

class UvmSequencer<Req: UvmSequenceItem>(REQ: Req): UvmSequencerParamBase<Req, Req>(REQ, REQ) {

    val seq_item_export = UvmSeqItemPullImp(REQ)
}

@Suppress("FunctionName")
fun <Req: UvmSequenceItem> i_UvmSequencer(REQ: Req) = UvmSequencer(REQ)
