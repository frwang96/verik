@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.ref.basic_intf

// Copyright (c) 2020 Francis Wang

infix fun _ms_if.con(block: (_ms_if) -> Unit) = this
infix fun _master.con(block: (_master) -> Unit) = this
infix fun _slave.con(block: (_slave) -> Unit) = this
infix fun _top.con(block: (_top) -> Unit) = this
infix fun _tb.con(block: (_tb) -> Unit) = this
