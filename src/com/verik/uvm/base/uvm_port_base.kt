@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.uvm.base

import com.verik.common.*
import com.verik.uvm.tlm1._uvm_tlm_if_base

// Copyright (c) 2020 Francis Wang

@virtual abstract class _uvm_port_base<REQ, RSP>: _uvm_tlm_if_base<REQ, RSP>() {

    fun <T: _uvm_port_base<REQ, RSP>>connect(provider: T) {}
}