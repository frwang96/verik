@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.uvm.tlm1

import com.verik.common.*
import com.verik.uvm.base._uvm_port_base
import com.verik.uvm.seq._uvm_sequence_item

// Copyright (c) 2020 Francis Wang

@extern class _uvm_seq_item_pull_port<_REQ : _uvm_sequence_item>(REQ: _REQ): _uvm_port_base<_REQ, _REQ>(REQ, REQ) {

    @task fun get_next_item() = REQ
}

@extern class _uvm_seq_item_pull_imp<_REQ : _uvm_sequence_item>(REQ: _REQ): _uvm_port_base<_REQ, _REQ>(REQ, REQ)