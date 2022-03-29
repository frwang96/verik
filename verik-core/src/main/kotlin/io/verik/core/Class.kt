/*
 * SPDX-License-Identifier: Apache-2.0
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
     * Randomize random properties in the class subject to the enabled [constraints][Cons].
     */
    fun randomize() {}

    /**
     * Configuration that should happen before a call to [randomize].
     */
    protected open fun preRandomize() {}

    /**
     * Configuration that should happen after a call to [randomize].
     */
    protected open fun postRandomize() {}
}
