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

@file:Suppress("UNUSED_PARAMETER")

package verik.common

fun log(x: Int): Int {
    if (x <= 0){
        throw IllegalArgumentException("value must be positive")
    }
    var n = x - 1
    var log = 0
    while (n != 0) {
        n = n shr 1
        log += 1
    }
    return log
}

fun exp(x: Int): Int {
    if (x < 0){
        throw IllegalArgumentException("value must not be negative")
    }
    if (x >= 31){
        throw IllegalArgumentException("value out of range")
    }
    return 1 shl x
}

fun min(x: Int, vararg y: Int): Int {
    var min = x
    y.forEach { if (it < min) min = it }
    return min
}

fun max(x: Int, vararg y: Int): Int {
    var max = x
    y.forEach { if (it > max) max = it }
    return max
}
