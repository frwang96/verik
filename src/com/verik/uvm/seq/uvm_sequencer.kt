@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.uvm.seq

import com.verik.common.*
import com.verik.uvm.tlm1._uvm_seq_item_pull_imp

// Copyright (c) 2020 Francis Wang

@virtual class _uvm_sequencer<REQ, RSP>: _uvm_sequencer_param_base<REQ, RSP>() {

    val seq_item_export = _uvm_seq_item_pull_imp<REQ, RSP>()
}