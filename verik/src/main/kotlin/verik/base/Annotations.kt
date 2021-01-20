/*
 * Copyright (c) 2020 Francis Wang
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

@file:Suppress("ClassName", "unused")

package verik.base

/**
 * Annotates top [module][Module] of hierarchy.
 */
@Target(AnnotationTarget.CLASS)
annotation class top

/**
 * Annotates input ports.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class input

/**
 * Annotates output ports.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class output

/**
 * (UNIMPLEMENTED) Annotates inout ports.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class inout

/**
 * (UNIMPLEMENTED) Annotates busses.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class bus

/**
 * (UNIMPLEMENTED) Annotates bus ports.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class busport

/**
 * (UNIMPLEMENTED) Annotates clock ports.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class clockport

/**
 * Annotates component instantiations.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class make

/**
 * Annotates combinational action blocks (corresponds to SystemVerilog always_comb blocks).
 */
@Target(AnnotationTarget.FUNCTION)
annotation class com

/**
 * Annotates sequential action blocks (corresponds to SystemVerilog always_ff blocks). Sequential action blocks
 * must contain an [on] expression to specify the sensitivity list.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class seq

/**
 * Annotates run action blocks (corresponds to SystemVerilog initial blocks).
 */
@Target(AnnotationTarget.FUNCTION)
annotation class run

/**
 * (UNIMPLEMENTED) Annotates tasks.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class task
