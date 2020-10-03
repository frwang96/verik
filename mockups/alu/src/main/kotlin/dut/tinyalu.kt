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

val LEN = 8

class _tinyalu: _module {
    @input  var clk    = _bool()
    @input  var reset  = _bool()
    @input  var a      = _uint(LEN)
    @input  var b      = _uint(LEN)
    @input  var op     = _uint(3)
    @input  var start  = _bool()
    @output var done   = _bool()
    @output var result = _uint(2 * LEN)

    var done_aax      = _bool()
    var done_mult     = _bool()
    var result_aax    = _uint(16)
    var result_mult   = _uint(16)
    var start_single  = _bool()
    var start_mult    = _bool()
    var done_internal = _bool()

    @make val add_and_xor = _add_and_xor() with {
        it.clk     += clk
        it.reset   += reset
        it.a       += a
        it.b       += b
        it.op      += op
        it.start   += start_single
        done_aax   += it.done_aax
        result_aax += it.result_aax
    }

    @make val pipelined_mult = _pipelined_mult() with {
        it.clk      += clk
        it.reset    += reset
        it.a        += a
        it.b        += b
        it.start    += start_mult
        done_mult   += it.done_mult
        result_mult += it.result_mult
    }

    @comb fun start_demux() {
        if (op[2]) {
            start_single += false
            start_mult += start
        } else {
            start_single += start
            start_mult += false
        }
    }

    @comb fun result_mux() {
        result += if (op[2]) result_mult else result_aax
    }

    @comb fun done_mux() {
        done_internal += if(op[2]) done_mult else done_aax
        done += done_internal
    }
}