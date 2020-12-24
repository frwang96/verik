package dut

import verik.base.*
import verik.data.*

@top class _multiplier: _module {

    @input var clk      = _bool()
    @input var a_in     = _ubit(8)
    @input var b_in     = _ubit(8)
    @output var res     = _ubit(16)
    @output var res_rdy = _bool()

    var a    = _ubit(8)
    var b    = _ubit(8)
    var prod = _ubit(8)
    var tp   = _ubit(8)
    var i    = _ubit(4)

    @seq fun mul_step() {
        on (posedge(clk)) {}
    }

    @com fun set_res () {
        res_rdy = (i == ubit(8))
    }
}
