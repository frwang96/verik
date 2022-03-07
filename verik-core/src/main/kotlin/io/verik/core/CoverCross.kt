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

@file:Suppress("UNUSED_PARAMETER", "unused")

package io.verik.core

/**
 * A cover cross within a [cover group][CoverGroup] that crosses a number of [cover points][CoverPoint].
 */
abstract class CoverCross {

    /**
     * Create a cover bin. [value] will be injected as SystemVerilog and used to specify the bin.
     */
    fun bin(name: String, value: String) {
        throw VerikException()
    }

    /**
     * Create an array of cover bins. [value] will be injected as SystemVerilog and used to specify the bins.
     */
    fun bins(name: String, value: String) {
        throw VerikException()
    }

    /**
     * Ignore a bin. [value] will be injected as SystemVerilog and used to specify the bin.
     */
    fun ignoreBin(name: String, value: String) {
        throw VerikException()
    }
}
