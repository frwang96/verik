package examples.buffer

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

@top class _buffer: _circuit {
    @input  val clk = _bool()
    @input  val sw  = _uint(16)
    @output val led = _uint(16)

    @put fun led() {
        led put sw
    }
}