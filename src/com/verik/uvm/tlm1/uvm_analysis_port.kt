@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.uvm.tlm1

import com.verik.common.*
import com.verik.uvm.base._uvm_port_base

// Copyright (c) 2020 Francis Wang

@virtual class _uvm_analysis_port<T>: _uvm_port_base<T, T>() {

    fun write(t: T) {}
}

@virtual class _uvm_analysis_imp<T>(callback: (T) -> Unit): _uvm_port_base<T, T>()
