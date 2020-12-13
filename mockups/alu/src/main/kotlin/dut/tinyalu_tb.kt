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

import verik.base.*
import verik.data.*

enum class _alu_op(override val value: _ubit): _enum {
    NOP(ubit(0b000)),
    ADD(ubit(0b001)),
    AND(ubit(0b010)),
    XOR(ubit(0b011)),
    MUL(ubit(0b100)),
    RST(ubit(0b111))
}

// TODO use object declaration
class _tb_util: _class {

    fun get_alu_op(): _alu_op {
        return when (random(8)) {
            0 -> _alu_op.NOP
            1 -> _alu_op.ADD
            2 -> _alu_op.AND
            3 -> _alu_op.XOR
            4 -> _alu_op.MUL
            5 -> _alu_op.NOP
            else -> _alu_op.RST
        }
    }

    fun get_data(zero: _ubit): _ubit {
        type(_ubit(LEN), _ubit(LEN))
        return when (random(4)) {
            0 -> zero
            1 -> ubit(-1)
            else -> ubit(random(exp(LEN)))
        }
    }
}

@top class _tb: _module {
    private var clk    = _bool()
    private var reset  = _bool()
    private var a      = _ubit(LEN)
    private var b      = _ubit(LEN)
    private var alu_op = _alu_op()
    private var start  = _bool()
    private var done   = _bool()
    private var result = _ubit(2 * LEN)

    private var op = com { alu_op.value }

    @make val tinyalu = _tinyalu() with {
        it.clk   = clk
        it.reset = reset
        it.a     = a
        it.b     = b
        it.op    = op
        it.start = start
        done     = it.done
        result   = it.result
    }

    @run fun clock() {
        clk = false
        forever {
            delay(10)
            clk = !clk
        }
    }

    @run fun tester() {
        reset = true
        repeat (2) { wait(negedge(clk)) }
        reset = true
        start = false
        repeat (1000) { send_op() }
    }

    @task fun send_op() {
        wait(negedge(clk))
        alu_op = _tb_util().get_alu_op()
        a = _tb_util().get_data(ubit(0))
        b = _tb_util().get_data(ubit(0))
        start = true
        when (alu_op) {
            _alu_op.NOP -> {
                wait(posedge(clk))
                start = false
            }
            _alu_op.RST -> {
                reset = true
                start = false
                wait(negedge(clk))
                reset = false
            }
            else -> {
                wait(posedge(done))
                start = false
            }
        }
    }

    @seq fun scoreboard() {
        on (posedge(clk)) {
            delay(1)
            val predicted_result = when (alu_op) {
                _alu_op.ADD -> ext(2 * LEN, a add b)
                _alu_op.AND -> ext(2 * LEN, a and b)
                _alu_op.XOR -> ext(2 * LEN, a xor b)
                _alu_op.MUL -> a mul b
                else -> ubit(0)
            }

            if (alu_op != _alu_op.NOP && alu_op != _alu_op.RST) {
                if (predicted_result != result) {
                    println("FAILED: A=$a B=$b op=$op result=$result")
                }
            }
        }
    }
}