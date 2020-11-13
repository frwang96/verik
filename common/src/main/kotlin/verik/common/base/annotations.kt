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

package verik.common.base

@Target(AnnotationTarget.CLASS)
annotation class top

@Target(AnnotationTarget.PROPERTY)
annotation class input

@Target(AnnotationTarget.PROPERTY)
annotation class output

@Target(AnnotationTarget.PROPERTY)
annotation class inout

@Target(AnnotationTarget.PROPERTY)
annotation class bus

@Target(AnnotationTarget.PROPERTY)
annotation class busport

@Target(AnnotationTarget.PROPERTY)
annotation class make

@Target(AnnotationTarget.PROPERTY)
annotation class wire

@Target(AnnotationTarget.FUNCTION)
annotation class com

@Target(AnnotationTarget.FUNCTION)
annotation class seq

@Target(AnnotationTarget.FUNCTION)
annotation class run

@Target(AnnotationTarget.FUNCTION)
annotation class task
