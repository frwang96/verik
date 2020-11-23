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

import verik.base.*
import verik.data.*

open class _parent(val SIZE: _int): _class {

    private var x = _uint(SIZE)

    fun init(x: _uint) {
        x type _uint(SIZE)
        this.x = x
    }
}

class _child(SIZE: _int): _parent(SIZE) {

    private var y = _uint(SIZE)

    fun init(x: _uint, y: _uint) {
        x type _uint(SIZE)
        y type _uint(SIZE)
        super.init(x)
        this.y = y
    }
}

@top class _top: _module {

    private var parent = _parent(8)
    private var child = _child(8)

    @run fun init() {
        parent = parent(8, uint(0))
        child = child(8, uint(0), uint(0))
    }
}
