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

package dut

import verik.common.*
import verik.common.data.*

open class _parent(val SIZE: _int): _class {

    val x = _uint(SIZE)
}

fun parent(SIZE: _int, x: _int) = _parent(SIZE) with {
    it.x put x
}

class _child(SIZE: _int): _parent(SIZE) {

    val y = _uint(SIZE)
}

fun child(SIZE: _int, x: _int, y: _int) = _child(SIZE) with {
    apply(parent(SIZE, x))
    it.y put y
}

@top class _top: _module {

    val parent = _parent(8)
    val child = _child(8)

    @run fun init() {
        parent put parent(8, 0)
        child put child(8, 0, 0)
    }
}
