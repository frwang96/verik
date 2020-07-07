package examples.buffer

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

@top class _buffer_outer: _circuit {
    @input  val clk = _bool()
    @input  val sw  = _uint(16)
    @output val led = _uint(16)

    @comp val buffer_inner = _buffer_inner() with {
        clk; sw; led
    }
}

@top class _buffer_inner: _circuit {
    @input  val clk = _bool()
    @input  val sw  = _uint(16)
    @output val led = _uint(16)

    @put fun led() {
        led put sw
    }
}
