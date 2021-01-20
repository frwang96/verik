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

package uvm.tlm1

import uvm.base.UvmPortBase
import uvm.seq.UvmSequenceItem

class UvmAnalysisPort<Req: UvmSequenceItem>(REQ: Req): UvmPortBase<Req, Req>(REQ, REQ) {

    fun write(req: Req) {}
}

@Suppress("FunctionName")
fun <Req: UvmSequenceItem> i_UvmAnalysisPort(REQ: Req) = UvmAnalysisPort(REQ)

open class UvmAnalysisImp<Req: UvmSequenceItem>(REQ: Req): UvmPortBase<Req, Req>(REQ, REQ) {

    open fun read(req: Req) {}
}

@Suppress("FunctionName")
fun <Req: UvmSequenceItem> i_UvmAnalysisImp(REQ: Req, callback: (Req) -> Unit) = UvmAnalysisImp(REQ)
