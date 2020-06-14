package com.verik.ref.basic_tb

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

enum class _alu_op(val bits: _bits): _enum {
    no_op  (_bits.of("3b'000")),
    add_op (_bits.of("3b'001")),
    and_op (_bits.of("3b'010")),
    xor_op (_bits.of("3b'011")),
    mul_op (_bits.of("3b'100")),
    rst_op (_bits.of("3b'111"));
    companion object {operator fun invoke() = values()[0]}
}

@main class _tb: _module {

    val A      = _uint(8)
    val B      = _uint(8)
    val clk    = _bool()
    val reset  = _bool()
    val op     = _bits(3)
    val start  = _bool()
    val done   = _bool()
    val result = _uint(16)
    val op_set = _alu_op()

    val tinyalu = _tinyalu()
    @connect fun DUT() {
        tinyalu con_name list(A, B, clk, op, reset, start, done, result)
    }

    @comb fun set_op() {
        op set op_set.bits
    }

    @initial fun clock() {
        clk set false
        forever {
            vk_delay(10)
            clk set !clk
        }
    }

    @function fun get_op(): _alu_op {
        val op = _alu_op()
        op set when (_bits.of(3, vk_random())) {
            _bits.of("3b'000") -> _alu_op.no_op
            _bits.of("3b'001") -> _alu_op.add_op
            _bits.of("3b'010") -> _alu_op.and_op
            _bits.of("3b'011") -> _alu_op.xor_op
            _bits.of("3b'100") -> _alu_op.mul_op
            _bits.of("3b'101") -> _alu_op.no_op
            else -> _alu_op.rst_op
        }
        return op
    }

    @function fun get_data(): _uint {
        val data = _uint(2)
        data set when (_bits.of(2, vk_random())){
            _bits.of("2b'00") -> _uint.of(8, 0)
            _bits.of("2b'11") -> _uint.of(8, -1)
            else -> _uint.of(8, vk_random())
        }
        return data
    }

    @seq fun scoreboard() {
        on (posedge(clk)) {
            vk_delay(1)
            val predicted_result = _uint(16)
            predicted_result set when (op_set) {
                _alu_op.add_op -> ext(16, A add B)
                _alu_op.and_op -> ext(16, A and B)
                _alu_op.xor_op -> ext(16, A xor B)
                _alu_op.mul_op -> A mul B
                else -> _uint.of(16, 0)
            }

            if (op_set !in listOf(_alu_op.no_op, _alu_op.rst_op)) {
                if (predicted_result != result) {
                    vk_error("FAILED: A=$A B=$B op=$op result=$result")
                }
            }
        }
    }

    @initial fun tester() {
        reset set true
        vk_wait_on(negedge(clk))
        vk_wait_on(negedge(clk))
        reset set true
        start set false
        repeat (1000) {
            send_op()
        }
    }

    @task fun send_op() {
        vk_wait_on(negedge(clk))
        op_set set get_op()
        A set get_data()
        B set get_data()
        start set true
        when (op_set) {
            _alu_op.no_op -> {
                vk_wait_on(posedge(clk))
                start set false
            }
            _alu_op.rst_op -> {
                reset set true
                start set false
                vk_wait_on(negedge(clk))
                reset set false
            }
            else -> {
                vk_wait_on(posedge(done))
                start set false
            }
        }
    }
}