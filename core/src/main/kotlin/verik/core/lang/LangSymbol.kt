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

import verik.core.main.symbol.Symbol


object LangSymbol {

    private object Indexer {

        private var index = 0

        fun next(): Symbol {
            index += 1
            return Symbol(-1, 1, index)
        }
    }

    val TYPE_UNIT = Indexer.next()
    val TYPE_INT = Indexer.next()

    val TYPE_BOOL = Indexer.next()
    val INSTANCE_BOOL = Indexer.next()
    val FUNCTION_BOOL_TYPE = Indexer.next()

    val TYPE_UINT = Indexer.next()
    val INSTANCE_UINT = Indexer.next()
    val FUNCTION_UINT_TYPE = Indexer.next()

    val TYPE_SINT = Indexer.next()
    val INSTANCE_SINT = Indexer.next()
    val FUNCTION_SINT_TYPE = Indexer.next()

    val FUNCTION_FINISH = Indexer.next()
}