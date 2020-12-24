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

package verikc.sv.ast

enum class SvOperatorType {
    NOT,
    DELAY,
    MUL,
    ADD,
    SUB,
    SHIFT_LEFT,
    SHIFT_RIGHT,
    EQUALITY,
    IF,
    ASSIGN_BLOCKING,
    ASSIGN_NONBLOCKING,
    POSEDGE,
    NEGEDGE;

    fun precedence(): Int {
        return when (this) {
            NOT, DELAY -> 1
            MUL -> 6
            ADD, SUB -> 7
            SHIFT_LEFT, SHIFT_RIGHT -> 8
            EQUALITY -> 10
            IF -> 17
            ASSIGN_BLOCKING, ASSIGN_NONBLOCKING, POSEDGE, NEGEDGE -> 18
        }
    }
}
