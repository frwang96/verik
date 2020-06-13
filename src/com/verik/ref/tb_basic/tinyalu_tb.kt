import com.verik.ref.tb_basic.tinyalu_dut.tinyalu

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

@Main class top: Module {

    enum class operation_t(val bits: Bits): Data {
        no_op  (Bits.of("3b'000")),
        add_op (Bits.of("3b'001")),
        and_op (Bits.of("3b'010")),
        xor_op (Bits.of("3b'011")),
        mul_op (Bits.of("3b'100")),
        rst_op (Bits.of("3b'111"));
        companion object {operator fun invoke() = values()[0]}
    }

    val A      = UNum(8)
    val B      = UNum(8)
    val clk    = Bool()
    val reset  = Bool()
    val op     = Bits(3)
    val start  = Bool()
    val done   = Bool()
    val result = UNum(16)
    val op_set = operation_t()

    val DUT = tinyalu()
    @Connect fun DUT() {
        DUT con listOf(A, B, clk, op, reset, start, done, result)
    }

    @Comb fun comb_op() {
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
        val op = operation_t()
        op set when (Bits.of(3, vkRandom())) {
            Bits.of("3b'000") -> operation_t.no_op
            Bits.of("3b'001") -> operation_t.add_op
            Bits.of("3b'010") -> operation_t.and_op
            Bits.of("3b'011") -> operation_t.xor_op
            Bits.of("3b'100") -> operation_t.mul_op
            Bits.of("3b'101") -> operation_t.no_op
            else -> operation_t.rst_op
        }
        return op
    }

    @Fun fun get_data(): UNum {
        val data = UNum(2)
        data set when (Bits.of(2, vkRandom())){
            Bits.of("2b'00") -> UNum.of(8, 0)
            Bits.of("2b'11") -> UNum.of(8, -1)
            else -> UNum.of(8, vkRandom())
        }
        return data
    }

    @Seq fun scoreboard() {
        on (PosEdge(clk)) {
            vkDelay(1)
            val predicted_result = UNum(16)
            predicted_result set when (op_set) {
                operation_t.add_op -> ext(16, A add B)
                operation_t.and_op -> ext(16, A and B)
                operation_t.xor_op -> ext(16, A xor B)
                operation_t.mul_op -> A mul B
                else -> UNum.of(16, 0)
            }

            if (op_set !in listOf(operation_t.no_op, operation_t.rst_op)) {
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