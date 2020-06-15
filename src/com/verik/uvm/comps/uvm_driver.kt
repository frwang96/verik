@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.uvm.comps

import com.verik.common.virtual
import com.verik.uvm.base._uvm_component
import com.verik.uvm.tlm1._uvm_seq_item_pull_port

// Copyright (c) 2020 Francis Wang

@virtual abstract class _uvm_driver<REQ, RSP>: _uvm_component() {

    val seq_item_port =_uvm_seq_item_pull_port<REQ, RSP>()
}
