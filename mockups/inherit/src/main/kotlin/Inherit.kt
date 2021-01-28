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

open class Parent(val SIZE: Int): Class() {

    private var x = t_Ubit(SIZE)

    fun init(x: Ubit) {
        type(x, t_Ubit(SIZE))
        this.x = x
    }
}

class Child(SIZE: Int): Parent(SIZE) {

    private var y = t_Ubit(SIZE)

    fun init(x: Ubit, y: Ubit) {
        type(x, t_Ubit(SIZE))
        type(y, t_Ubit(SIZE))
        super.init(x)
        this.y = y
    }
}

class Top: Module() {

    private var parent = t_Parent(8)
    private var child = t_Child(8)

    @run fun run() {
        parent = i_Parent(8, u(0))
        child = i_Child(8, u(0), u(0))
    }
}
