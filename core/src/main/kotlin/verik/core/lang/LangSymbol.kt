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
import verik.core.it.ItTypeClass
import verik.core.it.ItTypeReified


object LangSymbol {

    private object Indexer {

        private var index = 0

        fun next(): Symbol {
            index += 1
            return Symbol(-1, 1, index)
        }
    }

    // module base
    val TYPE_UNIT = Indexer.next()
    val TYPE_REIFIED_UNIT = ItTypeReified(TYPE_UNIT, ItTypeClass.UNIT, listOf())
    val TYPE_ANY = Indexer.next()

    // module common
    val TYPE_INSTANCE = Indexer.next()
    val TYPE_MODULE = Indexer.next()
    val TYPE_CLASS = Indexer.next()
    val FUNCTION_CON = Indexer.next()

    // module control
    val TYPE_EVENT = Indexer.next()
    val FUNCTION_DELAY = Indexer.next()
    val FUNCTION_POSEDGE = Indexer.next()
    val FUNCTION_NEGEDGE = Indexer.next()
    val OPERATOR_ON = Indexer.next()
    val OPERATOR_FOREVER = Indexer.next()
    val OPERATOR_IF = Indexer.next()
    val OPERATOR_IF_ELSE = Indexer.next()

    // module data
    val TYPE_DATA = Indexer.next()
    val TYPE_BOOL = Indexer.next()
    val FUNCTION_BOOL = Indexer.next()
    val TYPE_INT = Indexer.next()
    val FUNCTION_INT = Indexer.next()
    val TYPE_UINT = Indexer.next()
    val FUNCTION_UINT = Indexer.next()
    val TYPE_SINT = Indexer.next()
    val FUNCTION_SINT = Indexer.next()

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
    val OPERATOR_WITH = Indexer.next()
}