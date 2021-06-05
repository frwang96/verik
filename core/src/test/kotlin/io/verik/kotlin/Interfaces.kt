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
 * Classes that inherit from this interface can be represented as a sequence of elements that can be iterated over.
 * See Kotlin [documentation](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/).
 */
interface Iterable<out T>

/**
 * An iterator over a collection or another entity that can be represented as a sequence of elements. Allows to
 * sequentially access the elements.
 * See Kotlin [documentation](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/iterator.html).
 */
interface Iterator<out T>

/**
 * Classes which inherit from this interface have a defined total ordering between their instances.
 * See Kotlin [documentation](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-comparable/).
 */
interface Comparable<in T>