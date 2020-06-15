@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.uvm.base

// Copyright (c) 2020 Francis Wang

fun uvm_info(id: String, msg: String, verbosity: _uvm_verbosity) {}
fun uvm_warning(id: String, msg: String) {}
fun uvm_error(id: String, msg: String) {}
fun uvm_fatal(id: String, msg: String) {}
