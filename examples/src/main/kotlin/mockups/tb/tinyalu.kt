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

package mockups.tb

import io.verik.common.*
import io.verik.types.*

class _tinyalu: _circuit {
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

    @comp val add_and_xor = _add_and_xor() with {
        a; b; clk; op; reset
        done_aax; result_aax
        it.start con start_single
    }

    @comp val pipelined_mult = _pipelined_mult() with {
        a; b; clk; reset
        done_mult; result_mult
        it.start con start_mult
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