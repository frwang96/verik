@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.uvm.seq

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

@virtual abstract class _uvm_sequencer_param_base<REQ: _uvm_sequence_item, RSP: _uvm_sequence_item>(val req: REQ, val rsp: RSP): _uvm_sequencer_base()