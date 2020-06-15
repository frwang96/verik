@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.uvm.base

import com.verik.common.task
import com.verik.common.virtual

// Copyright (c) 2020 Francis Wang

@virtual abstract class _uvm_component: _uvm_report_object() {

    @task open fun connect_phase(phase: _uvm_phase) {}
    @task open fun run_phase(phase: _uvm_phase) {}
}
