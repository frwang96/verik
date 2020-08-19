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

import verik.core.it.ItTypeClass
import verik.core.it.ItTypeReified
import verik.core.base.Symbol


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
    val TYPE_INT = Indexer.next()
    val TYPE_REIFIED_INT = ItTypeReified(TYPE_INT, ItTypeClass.INT, listOf())

    // module common
    val TYPE_MODULE = Indexer.next()
    val TYPE_CLASS = Indexer.next()

    // module data
    val TYPE_BOOL = Indexer.next()
    val FUNCTION_BOOL = Indexer.next()
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
}