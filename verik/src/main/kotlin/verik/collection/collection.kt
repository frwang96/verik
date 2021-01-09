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

@file:Suppress("unused")

package verik.collection

import verik.base.*
import verik.data.*

/**
 * (UNIMPLEMENTED) A generic collection.
 */
abstract class _collection: _instance() {

    /**
     * (UNIMPLEMENTED) Returns true if the collection is empty.
     */
    fun is_empty(): _bool {
        throw VerikDslException()
    }

    /**
     * (UNIMPLEMENTED) Returns the size of the collection.
     */
    fun size(): _int {
        throw VerikDslException()
    }
}