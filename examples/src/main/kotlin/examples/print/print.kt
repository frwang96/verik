package examples.print

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

@top class _print: _module {

    @initial fun print() {
        vk_wait(1)
        vk_finish()
    }
}