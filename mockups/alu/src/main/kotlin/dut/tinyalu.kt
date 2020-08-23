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

package dut

import verik.common.*
import verik.common.data.*

class _tinyalu: _module {
    @input  val a      = _uint(8)
    @input  val b      = _uint(8)
    @input  val clk    = _bool()
    @input  val op     = _uint(3)
    @input  val reset  = _bool()
    @input  val start  = _bool()
    @output val done   = _bool()
    @output val result = _uint(16)

    val done_aax      = _bool()
    val done_mult     = _bool()
    val result_aax    = _uint(16)
    val result_mult   = _uint(16)
    val start_single  = _bool()
    val start_mult    = _bool()
    val done_internal = _bool()

    @make val add_and_xor = _add_and_xor() with {
        it.clk        con clk
        it.reset      con reset
        it.done_aax   con done_aax
        it.result_aax con result_aax
        it.a          con a
        it.b          con b
        it.op         con op
        it.start      con start_single
    }

    @make val pipelined_mult = _pipelined_mult() with {
        it.clk         con clk
        it.reset       con reset
        it.done_mult   con done_mult
        it.result_mult con result_mult
        it.a           con a
        it.b           con b
        it.start       con start_mult
    }

    @put fun start_demux() {
        if (op[2]) {
            start_single put false
            start_mult put start
        } else {
            start_single put start
            start_mult put false
        }
    }

    @put fun result_mux() {
        result put if (op[2]) result_mult else result_aax
    }

    @put fun done_mux() {
        done_internal put if(op[2]) done_mult else done_aax
        done put done_internal
    }
}