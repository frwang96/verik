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

package io.verik.kotlin

/**
 * The root of the Kotlin class hierarchy. Every Kotlin class has Any as a superclass.
 * See Kotlin [documentation](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/).
 */
abstract class Any

/**
 * The type with only one value: the Unit object. This type corresponds to the void type in Java.
 * See Kotlin [documentation](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/).
 */
class Unit private constructor()

/**
 * Nothing has no instances. You can use Nothing to represent "a value that never exists": for example, if a function
 * has the return type of Nothing, it means that it never returns.
 * See Kotlin [documentation](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing.html).
 */
class Nothing private constructor()

/**
 * The common base class of all enum classes.
 * See Kotlin [documentation](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/).
 */
abstract class Enum<E : Enum<E>> : Comparable<E>

/**
 * Represents a 32-bit signed integer.
 * See Kotlin [documentation](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/).
 */
class Int private constructor()

/**
 * Represents a value which is either true or false.
 * See Kotlin [documentation](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/).
 */
class Boolean private constructor()

/**
 * The String class represents character strings.
 * See Kotlin [documentation](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/).
 */
class String private constructor()
