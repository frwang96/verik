@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.uvm.base

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

@virtual class _uvm_phase: _uvm_object() {

    fun raise_objection(obj: _uvm_object) {}
    fun drop_objection(obj: _uvm_object) {}
}