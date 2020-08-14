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

package verik.core.sv

enum class SvOperatorType {

    // operator
    DELAY,
    NOT,
    ADD,
    SUB,
    MUL,
    AND,
    OR,
    IF,

    // assignment
    BASSIGN,
    NBASSIGN;

    fun precedence(): Int {
        return when (this) {
            DELAY -> 0
            NOT -> 2
            MUL -> 6
            ADD, SUB -> 7
            AND -> 15
            OR -> 16
            IF -> 17
            BASSIGN, NBASSIGN -> 18
        }
    }
}