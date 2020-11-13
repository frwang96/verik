/*
 * Copyright 2020 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("UNUSED_PARAMETER", "unused")

package uvm.base

import verik.common.base.*

@task fun run_test() {}

fun uvm_info(id: String, msg: String, verbosity: _uvm_verbosity) {}
fun uvm_warning(id: String, msg: String) {}
fun uvm_error(id: String, msg: String) {}
fun uvm_fatal(id: String, msg: String) {}
