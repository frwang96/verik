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

package uvm.seq

import io.verik.common.*

abstract class _uvm_sequence_base: _uvm_sequence_item() {

    @task fun start(sequencer: _uvm_sequencer_base) {}

    @task abstract fun body()

    @task fun start_item(item: _uvm_sequence_item) {}

    @task fun finish_item(item: _uvm_sequence_item) {}
}