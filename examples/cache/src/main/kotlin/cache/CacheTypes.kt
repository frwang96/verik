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

package cache

import io.verik.core.*

typealias ADDR_WIDTH = `6`
typealias DATA_WIDTh = `8`
typealias TAG_WIDTH = `3`
typealias INDEX_WIDTH = `3`

typealias UbitAddr = Ubit<ADDR_WIDTH>
typealias UbitData = Ubit<DATA_WIDTh>
typealias UbitTag = Ubit<TAG_WIDTH>
typealias UbitIndex = Ubit<INDEX_WIDTH>

enum class Op {
    NOP,
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

class Line(
    var status: Status,
    var tag: UbitTag,
    var data: UbitData
) : Struct()