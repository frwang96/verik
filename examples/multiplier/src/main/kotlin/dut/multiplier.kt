package dut

import verik.base.*
import verik.data.*

class _multiplier: _module {

    @input var clk      = _bool()
    @input var rst      = _bool()
    @input var in_a     = _ubit(8)
    @input var in_b     = _ubit(8)
    @input var in_vld   = _bool()
    @output var res     = _ubit(16)
    @output var res_rdy = _bool()

    var a    = _ubit(8)
    var b    = _ubit(8)
    var prod = _ubit(8)
    var tp   = _ubit(8)
    var i    = _ubit(4)

    @seq fun mul_step() {
        on (posedge(clk)) {
            if (rst) {
                a = ubit(0)
                b = ubit(0)
                prod = ubit(0)
                tp = ubit(0)
                i = ubit(0)
            } else {
                if (in_vld) {
                    a = in_a
                    b = in_b
                    prod = ubit(0)
                    tp = ubit(0)
                    i = ubit(0)
                } else if (i < ubit(8)) {
                    b = b sr 1
                    i += ubit(1)
                }
            }
        }
    }

    @com fun set_res () {
        res_rdy = (i == ubit(8))
        res = cat(tp, prod)
    }
}
