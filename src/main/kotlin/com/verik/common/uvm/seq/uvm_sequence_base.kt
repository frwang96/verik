@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.common.uvm.seq

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

@virtual abstract class _uvm_sequence_base: _uvm_sequence_item() {

    @task fun start(sequencer: _uvm_sequencer_base) {}

    @task abstract fun body()

    @task fun start_item(item: _uvm_sequence_item) {}

    @task fun finish_item(item: _uvm_sequence_item) {}
}