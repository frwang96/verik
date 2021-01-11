/*
 * Copyright (c) 2020 Francis Wang
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
    RETURN_VOID,
    RETURN,
    POSEDGE,
    NEGEDGE,
    SELECT_BIT,
    SELECT_PART,
    CONCATENATE,
    DELAY,
    AT,
    CAST_WIDTH,
    PRE_INCREMENT,
    PRE_DECREMENT,
    POST_INCREMENT,
    POST_DECREMENT,
    LOGICAL_NEGATION,
    BITWISE_NEGATION,
    REDUCTION_AND,
    REDUCTION_OR,
    REDUCTION_XOR,
    MUL,
    DIV,
    REM,
    ADD,
    SUB,
    SLL,
    SRL,
    SLA,
    SRA,
    GT,
    GEQ,
    LT,
    LEQ,
    EQ,
    NEQ,
    BITWISE_AND,
    BITWISE_XOR,
    BITWISE_OR,
    LOGICAL_AND,
    LOGICAL_OR,
    IF,
    ASSIGN_BLOCKING,
    ASSIGN_NONBLOCKING;

    fun precedence(): Int {
        return when (this) {
            RETURN_VOID, RETURN, POSEDGE, NEGEDGE -> 0
            SELECT_BIT, SELECT_PART, CONCATENATE, DELAY, AT, CAST_WIDTH -> 0
            PRE_INCREMENT, PRE_DECREMENT, POST_INCREMENT, POST_DECREMENT -> 0
            LOGICAL_NEGATION, BITWISE_NEGATION, REDUCTION_AND, REDUCTION_OR, REDUCTION_XOR -> 1
            MUL, DIV, REM -> 6
            ADD, SUB -> 7
            SLL, SRL, SLA, SRA -> 8
            GT, GEQ, LT, LEQ -> 9
            EQ, NEQ -> 10
            BITWISE_AND -> 12
            BITWISE_XOR -> 13
            BITWISE_OR -> 14
            LOGICAL_AND -> 15
            LOGICAL_OR -> 16
            IF -> 17
            ASSIGN_BLOCKING, ASSIGN_NONBLOCKING -> 18
        }
    }
}
