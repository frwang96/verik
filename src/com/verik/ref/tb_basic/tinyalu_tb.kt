import com.verik.ref.tb_basic.tinyalu_dut.tinyalu

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

class top: Module {

    enum class operation_t(val bits: Bits): Data {
        no_op  (Value(Bits(3), "000")),
        add_op (Value(Bits(3), "001")),
        and_op (Value(Bits(3), "010")),
        xor_op (Value(Bits(3), "011")),
        mul_op (Value(Bits(3), "100")),
        rst_op (Value(Bits(3), "111"));
        companion object {operator fun invoke() = values()[0]}
    }

    @Logic val A      = UNum(8)
    @Logic val B      = UNum(8)
    @Logic val clk    = Bool()
    @Logic val reset  = Bool()
    @Logic val op     = Bits(3)
    @Logic val start  = Bool()
    @Logic val done   = Bool()
    @Logic val result = UNum(16)
    @Logic val op_set = operation_t()

    val DUT = tinyalu()
    @Always fun connect() {
        DUT.connect(A, B, clk, op, reset, start, done, result)
        op set op_set.bits
    }

    @Initial fun clock() {
        clk set false
        forever {
            vkDelay(10)
            clk set !clk
        }
    }

    @Fun fun get_op(): operation_t {
        return when (Bits.of(3, vkRandom())) {
            Value(Bits(3), "000") -> operation_t.no_op
            Value(Bits(3), "001") -> operation_t.add_op
            Value(Bits(3), "010") -> operation_t.and_op
            Value(Bits(3), "011") -> operation_t.xor_op
            Value(Bits(3), "100") -> operation_t.mul_op
            Value(Bits(3), "101") -> operation_t.no_op
            else -> operation_t.rst_op
        }
    }

    @Fun fun get_data(): UNum {
        return when (Bits.of(2, vkRandom())){
            Value(Bits(2), "00") -> Value(UNum(8), 0)
            Value(Bits(2), "11") -> Value(UNum(8), -1)
            else -> UNum.of(8, vkRandom())
        }
    }

    @Always fun scoreboard() {
        on (PosEdge(clk)) {
            vkDelay(1)
            val predicted_result = UNum(16)
            predicted_result set when (op_set) {
                operation_t.add_op -> ext(16, A add B)
                operation_t.and_op -> ext(16, A and B)
                operation_t.xor_op -> ext(16, A xor B)
                operation_t.mul_op -> A mul B
                else -> Value(UNum(16), 0)
            }

            if ((op_set != operation_t.no_op) && (op_set != operation_t.rst_op)) {
                if (predicted_result != result) {
                    vkError("FAILED: A=$A B=$B op=$op result=$result")
                }
            }
        }
    }

    @Initial fun tester() {
        reset set true
        vkWaitOn(NegEdge(clk))
        vkWaitOn(NegEdge(clk))
        reset set true
        start set false
        repeat (1000) {
            send_op()
        }
    }

    @Task fun send_op() {
        vkWaitOn(NegEdge(clk))
        op_set set get_op()
        A set get_data()
        B set get_data()
        start set true
        when (op_set) {
            operation_t.no_op -> {
                vkWaitOn(PosEdge(clk))
                start set false
            }
            operation_t.rst_op -> {
                reset set true
                start set false
                vkWaitOn(NegEdge(clk))
                reset set false
            }
            else -> {
                vkWaitOn(PosEdge(done))
                start set false
            }
        }
    }
}