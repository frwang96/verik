@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.ref.basic_tb

// Copyright (c) 2020 Francis Wang

infix fun _add_and_xor.con(block: (_add_and_xor) -> Unit) = this
infix fun _pipelined_mult.con(block: (_pipelined_mult) -> Unit) = this
infix fun _tinyalu.con(block: (_tinyalu) -> Unit) = this
infix fun _tb.con(block: (_tb) -> Unit) = this

infix fun _alu_op.con(x: _alu_op?) {}
infix fun _alu_op.set(x: _alu_op?) = this
infix fun _alu_op.put(x: _alu_op?) {}
fun _alu_op() = _alu_op.values()[0]