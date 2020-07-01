@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.uvm.base

import com.verik.common.extern
import com.verik.uvm.seq._uvm_sequence_item
import com.verik.uvm.tlm1._uvm_tlm_if_base

// Copyright (c) 2020 Francis Wang

@extern abstract class _uvm_port_base<_REQ: _uvm_sequence_item, _RSP: _uvm_sequence_item>(REQ: _REQ, RSP: _RSP):_uvm_tlm_if_base<_REQ, _RSP>(REQ, RSP) {

    fun connect(provider: _uvm_port_base<_REQ, _RSP>) {}
}