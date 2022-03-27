/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused")

package io.verik.kotlin

/**
 * The common base class of all enum classes. Enums are declared with the enum keyword.
 *
 *      enum class Op {
 *          ADD,
 *          SUB,
 *          MUL
 *      }
 */
abstract class Enum<E : Enum<E>> : Comparable<E>

/**
 * Classes that inherit from this interface can be represented as a sequence of elements that can be iterated over.
 */
interface Iterable<out T>

/**
 * An iterator over a collection or another entity that can be represented as a sequence of elements. Allows to
 * sequentially access the elements.
 */
interface Iterator<out T>

/**
 * Classes which inherit from this interface have a defined total ordering between their instances.
 */
interface Comparable<in T>
