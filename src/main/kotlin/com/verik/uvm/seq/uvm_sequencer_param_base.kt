@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.uvm.seq

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

@extern abstract class _uvm_sequencer_param_base<_REQ: _uvm_sequence_item, _RSP: _uvm_sequence_item>(val REQ: _REQ, val RSP: _RSP): _uvm_sequencer_base()