@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.uvm.base

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

enum class _uvm_verbosity(val value: _uint = _enum.SEQUENTIAL): _enum {
    NONE,
    LOW,
    MEDIUM,
    HIGH,
    FULL,
    DEBUG
}
