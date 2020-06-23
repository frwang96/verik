@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.uvm.base

import com.verik.common.*
import com.verik.uvm.seq._uvm_sequence_item
import com.verik.uvm.tlm1._uvm_tlm_if_base

// Copyright (c) 2020 Francis Wang

@extern abstract class _uvm_port_base<REQ : _uvm_sequence_item, RSP : _uvm_sequence_item>(req: REQ, rsp: RSP): _uvm_tlm_if_base<REQ, RSP>(req, rsp) {

    fun <T: _uvm_port_base<REQ, RSP>> connect(provider: T) {}
}