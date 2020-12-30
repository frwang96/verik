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

import uvm.tlm1._uvm_seq_item_pull_imp

class _uvm_sequencer<REQ: _uvm_sequence_item>(_REQ: REQ): _uvm_sequencer_param_base<REQ, REQ>(_REQ, _REQ) {

    val seq_item_export = _uvm_seq_item_pull_imp(_REQ)
}

fun <REQ: _uvm_sequence_item> uvm_sequencer(_REQ: REQ) = _uvm_sequencer(_REQ)
