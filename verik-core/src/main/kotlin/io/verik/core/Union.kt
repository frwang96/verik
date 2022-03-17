/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.core

/**
 * The base class of all unions. Corresponds to SystemVerilog packed unions. All members of the union must have the same
 * width.
 */
abstract class Union {

    /**
     * (UNIMPLEMENTED) Returns true if any bit is unknown.
     */
    fun hasx(): Boolean {
        throw VerikException()
    }

    /**
     * (UNIMPLEMENTED) Returns true if any bit is floating.
     */
    fun hasz(): Boolean {
        throw VerikException()
    }

    /**
     * (UNIMPLEMENTED) Returns true if any bit is unknown or floating.
     */
    fun hasxz(): Boolean {
        throw VerikException()
    }
}
