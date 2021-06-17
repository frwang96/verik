/*
 * Copyright (c) 2021 Francis Wang
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
 * The base type for all user defined classes. They correspond to SystemVerilog classes.
 *
 *      class C: Class() {
 *
 *          fun f() {
 *              ...
 *          }
 *      }
 */
abstract class Class

/**
 * Returns the null object of class [C].
 */
fun <C : Class> n(): C {
    throw VerikException()
}
