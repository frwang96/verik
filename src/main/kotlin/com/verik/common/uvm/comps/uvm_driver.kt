@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.common.uvm.comps

import com.verik.common.*
import com.verik.common.uvm.base._uvm_component
import com.verik.common.uvm.seq._uvm_sequence_item
import com.verik.common.uvm.tlm1._uvm_seq_item_pull_port

// Copyright (c) 2020 Francis Wang

@virtual abstract class _uvm_driver<REQ: _uvm_sequence_item>(val req: REQ): _uvm_component() {

    val seq_item_port =_uvm_seq_item_pull_port(req)
}
