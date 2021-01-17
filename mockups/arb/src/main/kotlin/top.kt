/*
 * Copyright (c) 2021 Francis Wang
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

import verik.base.*
import verik.data.*

@top class _top: _module() {

    private var clk = _bool()

    @make val arb_bus = _arb_bus() with {
        it.clk = clk
    }

    @make val arb = _arb() with {
        it.arb_bp con arb_bus.dut_bp
    }

    @make val test = _test() with {
        it.arb_bp con arb_bus.test_bp
    }

    @run fun toggle_clk() {
        clk = false
        forever {
            delay(10)
            clk = !clk
        }
    }
}