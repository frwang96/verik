@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.uvm.seq

import com.verik.common.extern
import com.verik.uvm.tlm1._uvm_seq_item_pull_imp

// Copyright (c) 2020 Francis Wang

@extern class _uvm_sequencer<_REQ: _uvm_sequence_item>(REQ: _REQ): _uvm_sequencer_param_base<_REQ, _REQ>(REQ, REQ) {

    val seq_item_export = _uvm_seq_item_pull_imp(REQ)
}

fun <_REQ: _uvm_sequence_item> uvm_sequencer(REQ: _REQ) = _uvm_sequencer(REQ)
