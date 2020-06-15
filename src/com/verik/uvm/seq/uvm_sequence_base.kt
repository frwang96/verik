@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.uvm.seq

import com.verik.common.task
import com.verik.common.virtual

// Copyright (c) 2020 Francis Wang

@virtual abstract class _uvm_sequence_base: _uvm_sequence_item() {

    @task abstract fun body()

    @task fun start_item(item: _uvm_sequence_item) {}

    @task fun finish_item(item: _uvm_sequence_item) {}
}