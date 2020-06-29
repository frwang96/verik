@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.uvm.base

import com.verik.common._enum
import com.verik.common._uint
import com.verik.common.log

// Copyright (c) 2020 Francis Wang

enum class _uvm_verbosity(val value: _uint = _uint(log(values().size))): _enum {
    NONE,
    LOW,
    MEDIUM,
    HIGH,
    FULL,
    DEBUG
} fun _uvm_verbosity() = _enum(_uvm_verbosity.values())