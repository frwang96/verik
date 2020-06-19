@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.uvm.base

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

@virtual abstract class _uvm_object: _uvm_void() {

    open fun get_type_name() = ""
}
