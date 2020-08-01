/*
 * Copyright 2020 Francis Wang
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

import io.verik.common.*
import uvm.base._uvm_port_base
import uvm.seq._uvm_sequence_item

class _uvm_seq_item_pull_port<_REQ: _uvm_sequence_item>(REQ: _REQ): _uvm_port_base<_REQ, _REQ>(REQ, REQ) {

    @task fun get_next_item() = REQ
}

class _uvm_seq_item_pull_imp<_REQ: _uvm_sequence_item>(REQ: _REQ): _uvm_port_base<_REQ, _REQ>(REQ, REQ)