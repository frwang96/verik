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

import io.verik.common.*
import io.verik.common.data.*

@top class _buffer_outer: _module {
    @input  val sw  = _uint(16)
    @output val led = _uint(16)

    @comp val buffer_inner = _buffer_inner() with {
        it.sw con sw
        it.led con led
    }
}

class _buffer_inner: _module {
    @input  val sw  = _uint(16)
    @output val led = _uint(16)

    @put fun led() {
        led put sw
    }
}
