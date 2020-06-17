@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.uvm.tlm1

import com.verik.common.*
import com.verik.uvm.base._uvm_port_base
import com.verik.uvm.seq._uvm_sequence_item

// Copyright (c) 2020 Francis Wang

@virtual class _uvm_seq_item_pull_port<REQ : _uvm_sequence_item>(req: REQ): _uvm_port_base<REQ, REQ>(req, req) {

    @task fun get_next_item() = req
}

@virtual class _uvm_seq_item_pull_imp<REQ : _uvm_sequence_item>(req: REQ): _uvm_port_base<REQ, REQ>(req, req)