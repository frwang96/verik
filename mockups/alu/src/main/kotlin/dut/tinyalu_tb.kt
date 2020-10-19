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

enum class _alu_op(override val value: _int): _enum {
    NOP(0b000),
    ADD(0b001),
    AND(0b010),
    XOR(0b011),
    MUL(0b100),
    RST(0b111)
}

@top class _tb: _module {
    var clk    = _bool()
    var reset  = _bool()
    var a      = _uint(LEN)
    var b      = _uint(LEN)
    var alu_op = _alu_op()
    var start  = _bool()
    var done   = _bool()
    var result = _uint(2 * LEN)

    var op = com { alu_op.encoding() }

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

    fun get_data(zero: _uint): _uint {
        zero type _uint(LEN)
        RETURN type _uint(LEN)
        return when (random(4)) {
            1 -> zero
            2 -> uint(LEN, -1)
            else -> uint(LEN, random(exp(LEN)))
        }
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
        alu_op = get_alu_op()
        a = get_data(uint(LEN, 0))
        b = get_data(uint(LEN, 0))
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
                else -> uint(2 * LEN, 0)
            }

            if (alu_op neq _alu_op.NOP && alu_op neq _alu_op.RST) {
                if (predicted_result neq result) {
                    println("FAILED: A=$a B=$b op=$op result=$result")
                }
            }
        }
    }
}