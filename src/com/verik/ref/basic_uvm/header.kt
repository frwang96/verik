@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.ref.basic_uvm

// Copyright (c) 2020 Francis Wang

infix fun _reg_ctrl.con(block: (_reg_ctrl) -> Unit) = this
infix fun _reg_if.con(block: (_reg_ctrl) -> Unit) = this
infix fun _tb.con(block: (_reg_ctrl) -> Unit) = this
