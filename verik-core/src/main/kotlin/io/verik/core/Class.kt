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

@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package io.verik.core

/**
 * Base class for all user defined classes. Classes that inherit from [Class] correspond to SystemVerilog classes.
 *
 *  ```
 *  class C : Class()
 *  ```
 */
abstract class Class {

    /**
     * Randomize all random properties in the class subject to all enabled [constraints][Cons].
     */
    fun randomize() {}

    /**
     * Object configuration that should happen before a call to [randomize].
     */
    open fun preRandomize() {}

    /**
     * Object configuration that should happen after a call to [randomize].
     */
    open fun postRandomize() {}
}
