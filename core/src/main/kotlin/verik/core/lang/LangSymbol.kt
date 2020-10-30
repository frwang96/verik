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

package verik.core.lang

import verik.core.base.Symbol
import verik.core.rf.ast.RfReifiedType
import verik.core.rf.ast.RfTypeClass


object LangSymbol {

    private object Indexer {

        private var index = 0

        fun next(): Symbol {
            index += 1
            return Symbol(-1, 1, index)
        }
    }

    val SCOPE_LANG = Symbol(-1, 1, 0)

    // module base
    val TYPE_UNIT = Indexer.next()
    val TYPE_REIFIED_UNIT = RfReifiedType(TYPE_UNIT, RfTypeClass.INSTANCE, listOf())
    val TYPE_ANY = Indexer.next()

    // module common
    val TYPE_INSTANCE = Indexer.next()
    val TYPE_MODULE = Indexer.next()
    val TYPE_CLASS = Indexer.next()
    val FUNCTION_CON = Indexer.next()
    val OPERATOR_WITH = Indexer.next()

    // module enum
    val TYPE_ENUM = Indexer.next()
    val FUNCTION_ENUM_SEQUENTIAL = Indexer.next()
    val FUNCTION_ENUM_ONE_HOT = Indexer.next()
    val FUNCTION_ENUM_ZERO_ONE_HOT = Indexer.next()

    // module property
    val TYPE_X = Indexer.next()
    val PROPERTY_X = Indexer.next()

    // module control
    val TYPE_EVENT = Indexer.next()
    val FUNCTION_DELAY = Indexer.next()
    val FUNCTION_POSEDGE = Indexer.next()
    val FUNCTION_NEGEDGE = Indexer.next()
    val OPERATOR_ON = Indexer.next()
    val OPERATOR_COM = Indexer.next()
    val OPERATOR_SEQ = Indexer.next()
    val OPERATOR_FOREVER = Indexer.next()
    val OPERATOR_IF = Indexer.next()
    val OPERATOR_IF_ELSE = Indexer.next()

    // module assignment
    val FUNCTION_ASSIGN_BOOL_BOOL = Indexer.next()
    val FUNCTION_ASSIGN_UINT_INT = Indexer.next()
    val FUNCTION_ASSIGN_UINT_UINT = Indexer.next()

    // module data
    val TYPE_DATA = Indexer.next()
    val TYPE_BOOL = Indexer.next()
    val FUNCTION_TYPE_BOOL = Indexer.next()
    val TYPE_INT = Indexer.next()
    val FUNCTION_TYPE_INT = Indexer.next()
    val TYPE_UINT = Indexer.next()
    val FUNCTION_TYPE_UINT = Indexer.next()
    val FUNCTION_UINT_INT = Indexer.next()
    val TYPE_SINT = Indexer.next()
    val FUNCTION_TYPE_SINT = Indexer.next()
    val FUNCTION_SINT_INT = Indexer.next()

    // module functions native
    val FUNCTION_NATIVE_NOT = Indexer.next()
    val FUNCTION_NATIVE_ADD_INT_INT = Indexer.next()
    val FUNCTION_NATIVE_ADD_INT_UINT = Indexer.next()
    val FUNCTION_NATIVE_ADD_UINT_INT = Indexer.next()
    val FUNCTION_NATIVE_ADD_UINT_UINT = Indexer.next()

    // module string
    val TYPE_STRING = Indexer.next()
    val FUNCTION_PRINT = Indexer.next()
    val FUNCTION_PRINTLN = Indexer.next()

    // module system
    val FUNCTION_FINISH = Indexer.next()

    // operators
    val OPERATOR_RETURN_UNIT = Indexer.next()
    val OPERATOR_RETURN = Indexer.next()
    val OPERATOR_CONTINUE = Indexer.next()
    val OPERATOR_BREAK = Indexer.next()
    val OPERATOR_REPEAT = Indexer.next()
    val OPERATOR_FOR_EACH = Indexer.next()
    val OPERATOR_FOR_INDICES = Indexer.next()
    val OPERATOR_WHILE = Indexer.next()
    val OPERATOR_DO_WHILE = Indexer.next()
}