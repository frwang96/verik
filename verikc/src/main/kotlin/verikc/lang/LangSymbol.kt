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

package verikc.lang

import verikc.base.symbol.Symbol


object LangSymbol {

    private object Indexer {

        private var index = -1

        fun next(): Symbol {
            val symbol = Symbol(index)
            index -= 1
            return symbol
        }
    }

    val SCOPE_LANG = Indexer.next()

    // module base
    val TYPE_ANY = Indexer.next()
    val TYPE_UNIT = Indexer.next()
    val TYPE_INSTANCE = Indexer.next()
    val TYPE_COMPONENT = Indexer.next()
    val TYPE_MODULE = Indexer.next()
    val TYPE_CLASS = Indexer.next()
    val FUNCTION_CON_DATA_DATA = Indexer.next()
    val OPERATOR_WITH = Indexer.next()

    // module operator
    val FUNCTION_IF_ELSE = Indexer.next()
    val OPERATOR_RETURN_UNIT = Indexer.next()
    val OPERATOR_RETURN = Indexer.next()
    val OPERATOR_CONTINUE = Indexer.next()
    val OPERATOR_BREAK = Indexer.next()
    val OPERATOR_IF = Indexer.next()
    val OPERATOR_IF_ELSE = Indexer.next()
    val OPERATOR_FOR_EACH = Indexer.next()
    val OPERATOR_FOR_INDICES = Indexer.next()
    val OPERATOR_WHILE = Indexer.next()
    val OPERATOR_DO_WHILE = Indexer.next()

    // module control
    val TYPE_EVENT = Indexer.next()
    val FUNCTION_DELAY_INT = Indexer.next()
    val FUNCTION_WAIT_EVENT = Indexer.next()
    val FUNCTION_POSEDGE_BOOL = Indexer.next()
    val FUNCTION_NEGEDGE_BOOL = Indexer.next()
    val OPERATOR_ON = Indexer.next()
    val OPERATOR_COM = Indexer.next()
    val OPERATOR_SEQ = Indexer.next()
    val OPERATOR_FOREVER = Indexer.next()
    val OPERATOR_REPEAT = Indexer.next()

    // module system
    val FUNCTION_RANDOM = Indexer.next()
    val FUNCTION_FINISH = Indexer.next()

    // module data
    val TYPE_DATA = Indexer.next()
    val TYPE_LOGIC = Indexer.next()
    val FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE = Indexer.next()
    val FUNCTION_NATIVE_ASSIGN_BLOCKING = Indexer.next()
    val FUNCTION_NATIVE_ASSIGN_NONBLOCKING = Indexer.next()
    val FUNCTION_NATIVE_EQ_INSTANCE_INSTANCE = Indexer.next()
    val FUNCTION_NATIVE_NEQ_INSTANCE_INSTANCE = Indexer.next()

    // module misc
    val FUNCTION_CAT_DATA_DATA_VARARG = Indexer.next()

    // module bool
    val TYPE_BOOL = Indexer.next()
    val FUNCTION_TYPE_BOOL = Indexer.next()
    val FUNCTION_NATIVE_NOT_BOOL = Indexer.next()
    val FUNCTION_NATIVE_AND_BOOL_BOOL = Indexer.next()
    val FUNCTION_NATIVE_OR_BOOL_BOOL = Indexer.next()

    // module int
    val TYPE_INT = Indexer.next()
    val FUNCTION_TYPE_INT = Indexer.next()
    val FUNCTION_NATIVE_ADD_INT_INT = Indexer.next()
    val FUNCTION_NATIVE_SUB_INT_INT = Indexer.next()
    val FUNCTION_NATIVE_MUL_INT_INT = Indexer.next()

    // module string
    val TYPE_STRING = Indexer.next()
    val FUNCTION_PRINT_INSTANCE = Indexer.next()
    val FUNCTION_PRINTLN = Indexer.next()
    val FUNCTION_PRINTLN_INSTANCE = Indexer.next()

    // module ubit
    val TYPE_UBIT = Indexer.next()
    val FUNCTION_TYPE_UBIT = Indexer.next()
    val FUNCTION_UBIT_INT = Indexer.next()
    val FUNCTION_UBIT_INT_INT = Indexer.next()
    val FUNCTION_NATIVE_GT_UBIT_UBIT = Indexer.next()
    val FUNCTION_NATIVE_GEQ_UBIT_UBIT = Indexer.next()
    val FUNCTION_NATIVE_LT_UBIT_UBIT = Indexer.next()
    val FUNCTION_NATIVE_LEQ_UBIT_UBIT = Indexer.next()
    val FUNCTION_NATIVE_GET_UBIT_INT = Indexer.next()
    val FUNCTION_NATIVE_GET_UBIT_INT_INT = Indexer.next()
    val FUNCTION_NATIVE_NOT_UBIT = Indexer.next()
    val FUNCTION_NATIVE_ADD_UBIT_UBIT = Indexer.next()
    val FUNCTION_NATIVE_ADD_UBIT_SBIT = Indexer.next()
    val FUNCTION_NATIVE_SUB_UBIT_UBIT = Indexer.next()
    val FUNCTION_NATIVE_SUB_UBIT_SBIT = Indexer.next()
    val FUNCTION_NATIVE_MUL_UBIT_UBIT = Indexer.next()
    val FUNCTION_NATIVE_MUL_UBIT_SBIT = Indexer.next()
    val FUNCTION_ADD_UBIT_UBIT = Indexer.next()
    val FUNCTION_ADD_UBIT_SBIT = Indexer.next()
    val FUNCTION_SUB_UBIT_UBIT = Indexer.next()
    val FUNCTION_SUB_UBIT_SBIT = Indexer.next()
    val FUNCTION_MUL_UBIT_UBIT = Indexer.next()
    val FUNCTION_MUL_UBIT_SBIT = Indexer.next()
    val FUNCTION_SL_UBIT_INT = Indexer.next()
    val FUNCTION_SR_UBIT_INT = Indexer.next()
    val FUNCTION_AND_UBIT_UBIT = Indexer.next()
    val FUNCTION_AND_UBIT_SBIT = Indexer.next()
    val FUNCTION_OR_UBIT_UBIT = Indexer.next()
    val FUNCTION_OR_UBIT_SBIT = Indexer.next()
    val FUNCTION_XOR_UBIT_UBIT = Indexer.next()
    val FUNCTION_XOR_UBIT_SBIT = Indexer.next()
    val FUNCTION_INV_UBIT = Indexer.next()
    val FUNCTION_RED_AND_UBIT = Indexer.next()
    val FUNCTION_RED_OR_UBIT = Indexer.next()
    val FUNCTION_RED_XOR_UBIT = Indexer.next()
    val FUNCTION_EXT_UBIT_INT = Indexer.next()
    val FUNCTION_TRU_UBIT_INT = Indexer.next()

    // module sbit
    val TYPE_SBIT = Indexer.next()
    val FUNCTION_TYPE_SBIT = Indexer.next()
    val FUNCTION_SBIT_INT = Indexer.next()
    val FUNCTION_SBIT_INT_INT = Indexer.next()
    val FUNCTION_NATIVE_GT_SBIT_SBIT = Indexer.next()
    val FUNCTION_NATIVE_GEQ_SBIT_SBIT = Indexer.next()
    val FUNCTION_NATIVE_LT_SBIT_SBIT = Indexer.next()
    val FUNCTION_NATIVE_LEQ_SBIT_SBIT = Indexer.next()
    val FUNCTION_NATIVE_GET_SBIT_INT = Indexer.next()
    val FUNCTION_NATIVE_GET_SBIT_INT_INT = Indexer.next()
    val FUNCTION_NATIVE_NOT_SBIT = Indexer.next()
    val FUNCTION_NATIVE_ADD_SBIT_SBIT = Indexer.next()
    val FUNCTION_NATIVE_ADD_SBIT_UBIT = Indexer.next()
    val FUNCTION_NATIVE_SUB_SBIT_UBIT = Indexer.next()
    val FUNCTION_NATIVE_SUB_SBIT_SBIT = Indexer.next()
    val FUNCTION_NATIVE_MUL_SBIT_UBIT = Indexer.next()
    val FUNCTION_NATIVE_MUL_SBIT_SBIT = Indexer.next()
    val FUNCTION_ADD_SBIT_UBIT = Indexer.next()
    val FUNCTION_ADD_SBIT_SBIT = Indexer.next()
    val FUNCTION_SUB_SBIT_UBIT = Indexer.next()
    val FUNCTION_SUB_SBIT_SBIT = Indexer.next()
    val FUNCTION_MUL_SBIT_UBIT = Indexer.next()
    val FUNCTION_MUL_SBIT_SBIT = Indexer.next()
    val FUNCTION_SL_SBIT_INT = Indexer.next()
    val FUNCTION_SR_SBIT_INT = Indexer.next()
    val FUNCTION_AND_SBIT_UBIT = Indexer.next()
    val FUNCTION_AND_SBIT_SBIT = Indexer.next()
    val FUNCTION_OR_SBIT_UBIT = Indexer.next()
    val FUNCTION_OR_SBIT_SBIT = Indexer.next()
    val FUNCTION_XOR_SBIT_UBIT = Indexer.next()
    val FUNCTION_XOR_SBIT_SBIT = Indexer.next()
    val FUNCTION_INV_SBIT = Indexer.next()
    val FUNCTION_RED_AND_SBIT = Indexer.next()
    val FUNCTION_RED_OR_SBIT = Indexer.next()
    val FUNCTION_RED_XOR_SBIT = Indexer.next()
    val FUNCTION_EXT_SBIT_INT = Indexer.next()
    val FUNCTION_TRU_SBIT_INT = Indexer.next()

    // module enum
    val TYPE_ENUM = Indexer.next()
    val FUNCTION_ENUM_SEQUENTIAL = Indexer.next()
    val FUNCTION_ENUM_ONE_HOT = Indexer.next()
    val FUNCTION_ENUM_ZERO_ONE_HOT = Indexer.next()
}
