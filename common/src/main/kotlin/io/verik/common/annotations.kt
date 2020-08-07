/*
 * Copyright 2020 Francis Wang
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

package io.verik.common

/**
 * Mark module as top
 */
@Target(AnnotationTarget.CLASS)
annotation class top

/**
 * Mark class or struct as export
 */
@Target(AnnotationTarget.CLASS)
annotation class export

/**
 * Mark class or function as abstract
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class abstract

/**
 * Mark port as input
 */
@Target(AnnotationTarget.PROPERTY)
annotation class input

/**
 * Mark port as output
 */
@Target(AnnotationTarget.PROPERTY)
annotation class output

/**
 * Mark port as input and output
 */
@Target(AnnotationTarget.PROPERTY)
annotation class inout

/**
 * Mark port as interface
 */
@Target(AnnotationTarget.PROPERTY)
annotation class interf

/**
 * Mark port as modport
 */
@Target(AnnotationTarget.PROPERTY)
annotation class modport

/**
 * Mark component declaration
 */
@Target(AnnotationTarget.PROPERTY)
annotation class comp

/**
 * Mark variable as wire
 */
@Target(AnnotationTarget.PROPERTY)
annotation class wire

/**
 * Mark variable as rand
 */
@Target(AnnotationTarget.PROPERTY)
annotation class rand

/**
 * Mark function as put block
 */
@Target(AnnotationTarget.FUNCTION)
annotation class put

/**
 * Mark function as reg block
 */
@Target(AnnotationTarget.FUNCTION)
annotation class reg

/**
 * Mark function as drive block
 */
@Target(AnnotationTarget.FUNCTION)
annotation class drive

/**
 * Mark function as initial block
 */
@Target(AnnotationTarget.FUNCTION)
annotation class initial

/**
 * Mark function as task
 */
@Target(AnnotationTarget.FUNCTION)
annotation class task
