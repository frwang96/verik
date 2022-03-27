/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused")

package io.verik.kotlin

/**
 * The root of the Verik class hierarchy. Every Verik class has Any as a superclass.
 */
abstract class Any

/**
 * The type with only one value: the Unit object.
 */
class Unit private constructor()

/**
 * Nothing has no instances. You can use Nothing to represent "a value that never exists": for example, if a function
 * has the return type of Nothing, it means that it never returns.
 */
class Nothing private constructor()
