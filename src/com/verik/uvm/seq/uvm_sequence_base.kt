package com.verik.uvm.seq

import com.verik.common.task
import com.verik.common.virtual

// Copyright (c) 2020 Francis Wang

@virtual abstract class _uvm_sequence_base: _uvm_sequence_item() {

    @task abstract fun body()
}