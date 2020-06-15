@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.uvm.tlm1

import com.verik.common.task
import com.verik.common.virtual
import com.verik.uvm.base._uvm_port_base

// Copyright (c) 2020 Francis Wang

@virtual class _uvm_seq_item_pull_port<REQ, RSP>: _uvm_port_base<REQ, RSP>() {

    @task
    fun get_next_item(req_arg: REQ) {}
}