@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.uvm.tlm1

import com.verik.common.*
import com.verik.uvm.base._uvm_port_base
import com.verik.uvm.seq._uvm_sequence_item

// Copyright (c) 2020 Francis Wang

@extern class _uvm_analysis_port<REQ : _uvm_sequence_item>(req: REQ): _uvm_port_base<REQ, REQ>(req, req) {

    fun write(req: REQ) {}
}

@extern class _uvm_analysis_imp<REQ : _uvm_sequence_item>(req: REQ): _uvm_port_base<REQ, REQ>(req, req) {

    fun new(callback: (REQ) -> Unit) {}
}
