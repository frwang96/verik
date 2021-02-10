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

open class Parent: Class() {

    var x = t_Int()

    fun init(x: Int) {
        this.x = x
    }
}

class Child: Parent() {

    var y = t_Int()

    fun init(x: Int, y: Int) {
        super.init(x)
        this.y = y
    }
}

class Top: Module() {

    var parent = t_Parent()
    var child = t_Child()

    @run fun run() {
        parent = i_Parent(0)
        child = i_Child(0, 0)
    }
}
