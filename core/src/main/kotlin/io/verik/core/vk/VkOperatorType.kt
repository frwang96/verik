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

package io.verik.core.vk

import io.verik.core.LinePos
import io.verik.core.LinePosException

enum class VkOperatorType {

    // control flow
    IF,
    IF_ELSE,

    // operator
    GET,
    UNTIL,
    RANGE_TO,
    UNARY_PLUS,
    UNARY_MINUS,
    NOT,
    ADD_TRU,
    SUB_TRU,
    MUL_TRU,
    MOD,
    DIV,
    LT,
    GT,
    LTE,
    GTE,

    // infix
    IN,
    EQ,
    NEQ,
    ADD,
    SUB,
    MUL,
    SL,
    SR,
    ROTL,
    ROTR,
    SL_EXT,
    SR_TRU,
    AND,
    OR,
    XOR,
    NAND,
    NOR,
    XNOR,
    CAT,

    // assignment
    WITH,
    CON,
    PUT,
    REG,
    DRIVE,
    PUT_ADD,
    REG_ADD,
    PUT_SUB,
    REG_SUB,
    PUT_MUL,
    REG_MUL,
    PUT_SL,
    REG_SL,
    PUT_SR,
    REG_SR,
    PUT_ROTL,
    REG_ROTL,
    PUT_ROTR,
    REG_ROTR,
    PUT_AND,
    REG_AND,
    PUT_OR,
    REG_OR,
    PUT_XOR,
    REG_XOR,
    PUT_NAND,
    REG_NAND,
    PUT_NOR,
    REG_NOR,
    PUT_XNOR,
    REG_XNOR;

    companion object {

        fun infixType(identifier: String, linePos: LinePos): VkOperatorType {
            return when (identifier) {
                "with" -> WITH
                "con" -> CON
                "put" -> PUT
                "reg" -> REG
                "drive" -> DRIVE
                "until" -> UNTIL
                "eq" -> EQ
                "neq" -> NEQ
                "add" -> ADD
                "sub" -> SUB
                "mul" -> MUL
                "sl" -> SL
                "sr" -> SR
                "rotl" -> ROTL
                "rotr" -> ROTR
                "sl_ext" -> SL_EXT
                "sr_tru" -> SR_TRU
                "and" -> AND
                "or" -> OR
                "xor" -> XOR
                "nand" -> NAND
                "nor" -> NOR
                "xnor" -> XNOR
                "cat" -> CAT
                "put_add" -> PUT_ADD
                "reg_add" -> REG_ADD
                "put_sub" -> PUT_SUB
                "reg_sub" -> REG_SUB
                "put_mul" -> PUT_MUL
                "reg_mul" -> REG_MUL
                "put_sl" -> PUT_SL
                "reg_sl" -> REG_SL
                "put_sr" -> PUT_SR
                "reg_sr" -> REG_SR
                "put_rotl" -> PUT_ROTL
                "reg_rotl" -> REG_ROTL
                "put_rotr" -> PUT_ROTR
                "reg_rotr" -> REG_ROTR
                "put_and" -> PUT_AND
                "reg_and" -> REG_AND
                "put_or" -> PUT_OR
                "reg_or" -> REG_OR
                "put_xor" -> PUT_XOR
                "reg_xor" -> REG_XOR
                "put_nand" -> PUT_NAND
                "reg_nand" -> REG_NAND
                "put_nor" -> PUT_NOR
                "reg_nor" -> REG_NOR
                "put_xnor" -> PUT_XNOR
                "reg_xnor" -> REG_XNOR
                else -> throw LinePosException("infix operator $identifier not recognized", linePos)
            }
        }
    }
}