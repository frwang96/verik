@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.uvm.tlm1

import com.verik.common.extern
import com.verik.uvm.base._uvm_port_base
import com.verik.uvm.seq._uvm_sequence_item

// Copyright (c) 2020 Francis Wang

@extern class _uvm_analysis_port<_REQ: _uvm_sequence_item>(REQ: _REQ): _uvm_port_base<_REQ, _REQ>(REQ, REQ) {

    fun write(req: _REQ) {}
}

fun <_REQ: _uvm_sequence_item> uvm_analysis_port(REQ: _REQ) = _uvm_analysis_port(REQ)

@extern class _uvm_analysis_imp<_REQ: _uvm_sequence_item>(REQ: _REQ): _uvm_port_base<_REQ, _REQ>(REQ, REQ)

fun <_REQ: _uvm_sequence_item> uvm_analysis_imp(REQ: _REQ, callback: (_REQ) -> Unit) = _uvm_analysis_imp(REQ)
