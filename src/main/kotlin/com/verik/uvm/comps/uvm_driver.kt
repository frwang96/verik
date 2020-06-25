@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.uvm.comps

import com.verik.common.*
import com.verik.uvm.base._uvm_component
import com.verik.uvm.seq._uvm_sequence_item
import com.verik.uvm.tlm1._uvm_seq_item_pull_port

// Copyright (c) 2020 Francis Wang

@extern abstract class _uvm_driver<_REQ: _uvm_sequence_item>(val REQ: _REQ): _uvm_component() {

    val seq_item_port =_uvm_seq_item_pull_port(REQ)
}
