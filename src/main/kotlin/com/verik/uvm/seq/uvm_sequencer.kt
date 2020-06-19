@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.uvm.seq

import com.verik.common.*
import com.verik.uvm.tlm1._uvm_seq_item_pull_imp

// Copyright (c) 2020 Francis Wang

@virtual class _uvm_sequencer<REQ : _uvm_sequence_item>(req: REQ): _uvm_sequencer_param_base<REQ, REQ>(req, req) {

    val seq_item_export = _uvm_seq_item_pull_imp(req)
}