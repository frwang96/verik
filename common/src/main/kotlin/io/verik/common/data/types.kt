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

package io.verik.common.data

import java.util.*

class _byte internal constructor(bits: BitSet): _sint(8, bits) {

    constructor(): this(BitSet(0))
}

fun byte(value: Int): _byte {
    return _byte(getBits(8, value))
}

class _short internal constructor(bits: BitSet): _sint(16, bits) {

    constructor(): this(BitSet(0))
}

fun short(value: Int): _short {
    return _short(getBits(16, value))
}

class _int internal constructor(bits: BitSet): _sint(32, bits) {

    constructor(): this(BitSet(0))
}

fun int(value: Int): _int {
    return _int(getBits(32, value))
}
