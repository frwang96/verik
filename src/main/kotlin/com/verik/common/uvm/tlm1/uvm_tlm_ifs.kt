@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.common.uvm.tlm1

import com.verik.common.*
import com.verik.common.uvm.seq._uvm_sequence_item

// Copyright (c) 2020 Francis Wang

@virtual abstract class _uvm_tlm_if_base<REQ: _uvm_sequence_item, RSP: _uvm_sequence_item>(val req: REQ, val rsp: RSP): _class