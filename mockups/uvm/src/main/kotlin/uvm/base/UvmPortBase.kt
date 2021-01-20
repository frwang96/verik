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

package uvm.base

import uvm.seq.UvmSequenceItem
import uvm.tlm1.UvmTlmIfBase

abstract class UvmPortBase<Req: UvmSequenceItem, Rsp: UvmSequenceItem>
    (REQ: Req, RSP: Rsp): UvmTlmIfBase<Req, Rsp>(REQ, RSP) {

    fun connect(provider: UvmPortBase<Req, Rsp>) {}
}