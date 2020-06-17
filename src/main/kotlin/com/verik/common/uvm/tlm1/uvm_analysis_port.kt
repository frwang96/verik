@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.common.uvm.tlm1

import com.verik.common.*
import com.verik.common.uvm.base._uvm_port_base
import com.verik.common.uvm.seq._uvm_sequence_item

// Copyright (c) 2020 Francis Wang

@virtual class _uvm_analysis_port<REQ : _uvm_sequence_item>(req: REQ): _uvm_port_base<REQ, REQ>(req, req) {

    fun write(req: REQ) {}
}

@virtual class _uvm_analysis_imp<REQ : _uvm_sequence_item>(req: REQ): _uvm_port_base<REQ, REQ>(req, req) {

    fun new(callback: (REQ) -> Unit) {}
}
