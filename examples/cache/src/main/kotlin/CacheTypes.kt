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

val ADDR_WIDTH = 6
val DATA_WIDTH = 8
val TAG_WIDTH = 3
val INDEX_WIDTH = 3

@alias fun t_UbitAddr() = t_Ubit(ADDR_WIDTH)
@alias fun t_UbitData() = t_Ubit(DATA_WIDTH)
@alias fun t_UbitTag() = t_Ubit(TAG_WIDTH)
@alias fun t_UbitIndex() = t_Ubit(INDEX_WIDTH)

enum class Op {
    INVALID,
    READ,
    WRITE
}

enum class State {
    READY,
    WRITEBACK,
    FILL
}

enum class Status {
    INVALID,
    CLEAN,
    DIRTY
}

class Line: Struct() {

    var status = t_Status()
    var tag    = t_UbitTag()
    var data   = t_UbitData()
}