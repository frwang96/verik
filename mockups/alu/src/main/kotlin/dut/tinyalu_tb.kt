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

enum class _alu_op(override val value: _uint = _uint(3)): _enum {
    NOP(uint(0b000)),
    ADD(uint(0b001)),
    AND(uint(0b010)),
    XOR(uint(0b011)),
    MUL(uint(0b100)),
    RST(uint(0b111))
}

@top class _tb: _module {
    val a      = _uint(8)
    val b      = _uint(8)
    val clk    = _bool()
    val reset  = _bool()
    val start  = _bool()
    val done   = _bool()
    val result = _uint(16)
    val op_set = _alu_op()

    val op = op_set.value

    @make val tinyalu = _tinyalu() with {
        it.clk    con clk
        it.reset  con reset
        it.a      con a
        it.b      con b
        it.start  con start
        it.done   con done
        it.result con result
    }

    @run fun clock() {
        clk put false
        forever {
            delay(10)
            clk put !clk
        }
    }

    fun get_op(): _alu_op {
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

    fun get_data(): _uint {
        return when (random(4)) {
            1 -> uint(0x00)
            2 -> uint(0xFF)
            else -> uint(8, random(256))
        }
    }

    @reg fun scoreboard() {
        on (posedge(clk)) {
            delay(1)
            val predicted_result = when (op_set) {
                _alu_op.ADD -> ext(16, a add b)
                _alu_op.AND -> ext(16, a and b)
                _alu_op.XOR -> ext(16, a xor b)
                _alu_op.MUL -> a mul b
                else -> uint(16, 0)
            }

            if (op_set neq _alu_op.NOP && op_set neq _alu_op.RST) {
                if (predicted_result neq result) {
                    println("FAILED: A=$a B=$b op=$op result=$result")
                }
            }
        }
    }

    @run fun tester() {
        reset put true
        wait(negedge(clk))
        wait(negedge(clk))
        reset put true
        start put false
        repeat (1000) {
            send_op()
        }
    }

    @task fun send_op() {
        wait(negedge(clk))
        op_set put get_op()
        a put get_data()
        b put get_data()
        start put true
        when (op_set) {
            _alu_op.NOP -> {
                wait(posedge(clk))
                start put false
            }
            _alu_op.RST -> {
                reset put true
                start put false
                wait(negedge(clk))
                reset put false
            }
            else -> {
                wait(posedge(done))
                start put false
            }
        }
    }
}