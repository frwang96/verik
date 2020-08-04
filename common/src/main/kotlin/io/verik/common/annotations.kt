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

@Target(AnnotationTarget.CLASS)
annotation class top

@Target(AnnotationTarget.PROPERTY)
annotation class input

@Target(AnnotationTarget.PROPERTY)
annotation class output

@Target(AnnotationTarget.PROPERTY)
annotation class inout

@Target(AnnotationTarget.PROPERTY)
annotation class interf

@Target(AnnotationTarget.PROPERTY)
annotation class modport

@Target(AnnotationTarget.PROPERTY)
annotation class comp

@Target(AnnotationTarget.PROPERTY)
annotation class wire

@Target(AnnotationTarget.PROPERTY)
annotation class rand

@Target(AnnotationTarget.FUNCTION)
annotation class put

@Target(AnnotationTarget.FUNCTION)
annotation class reg

@Target(AnnotationTarget.FUNCTION)
annotation class drive

@Target(AnnotationTarget.FUNCTION)
annotation class initial

@Target(AnnotationTarget.FUNCTION)
annotation class task
