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

@file:Suppress("ClassName", "unused")

package io.verik.core

/**
 * Annotates Verik source files. This enables inspections by the
 * [Verik IntelliJ plugin](https://plugins.jetbrains.com/plugin/18275-verik).
 *
 * Verik source files should begin with the Verik annotation and an import for `io.verik.core`.
 *
 *  ```
 *  @file:Verik
 *
 *  import io.verik.core.*
 *  ```
 */
@Target(AnnotationTarget.FILE)
annotation class Verik

/**
 * Annotates component instantiations. We can instantiate a component with either positional port connections or named
 * port connections.
 *
 *  ```
 *  class M(
 *      @In clk: Boolean,
 *      @In rst: Boolean
 *  ) : Module()
 *
 *  class Top : Module() {
 *
 *      var clk: Boolean = nc()
 *      var rst: Boolean = nc()
 *
 *      @Make
 *      val m0 = M(clk, rst)
 *
 *      @Make
 *      val m1 = M(
 *          clk = clk,
 *          rst = rst
 *      )
 *  }
 *  ```
 */
@Target(AnnotationTarget.PROPERTY)
annotation class Make

/**
 * Annotates that a declaration is eligible to be an entry point. If dead code elimination is enabled, declarations that
 * are not reachable from any entry point are eliminated.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class EntryPoint

/**
 * Inject a string literal directly as SystemVerilog.
 *
 *  ```
 *  class C {
 *
 *      @Inject
 *      val macro = "`macro(C);"
 *  }
 *  ```
 */
@Target(AnnotationTarget.PROPERTY)
annotation class Inject

/**
 * Annotates input ports of a component. Mutable input ports are declared as var. Immutable input ports are declared as
 * val and must be assigned a constant expression when instantiated.
 *
 *  ```
 *  class M(@In var x: Boolean) : Module()
 *  ```
 */
@Target(AnnotationTarget.PROPERTY)
annotation class In

/**
 * Annotates output ports of a component. Output ports must be declared as var.
 *
 *  ```
 *  class M(@Out var x: Boolean) : Module()
 *  ```
 */
@Target(AnnotationTarget.PROPERTY)
annotation class Out

/**
 * Annotates combinational action blocks or combinationally assigned properties. It is executed when the value of
 * its contents change and is used to model combinational logic. They correspond to SystemVerilog always_comb blocks.
 *
 *  ```
 *  @Com
 *  fun f() {
 *      x = !y
 *  }
 *
 *  @Com
 *  val x = !y
 *  ```
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class Com

/**
 * Annotates sequential action blocks or sequentially assigned properties. It is executed when its event expression is
 * triggered and is used to model sequential logic. Sequential action blocks must contain an [on] expression and
 * sequentially assigned properties must contain an [oni] expression to specify their event expressions. They
 * correspond to SystemVerilog always_ff blocks.
 *
 *  ```
 *  @Seq
 *  fun f() {
 *      on (posedge(clk)) {
 *          x = !y
 *      }
 *  }
 *
 *  @Seq
 *  var x = oni(posedge(clk)) { !y }
 *  ```
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class Seq

/**
 * Annotates run action blocks. Run action blocks are started at the beginning of the simulation and are executed only
 * once during the entire simulation. They correspond to SystemVerilog initial blocks.
 *
 *  ```
 *  @Run
 *  fun f() {
 *      delay(100)
 *      finish()
 *  }
 *  ```
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Run

/**
 * Annotates tasks. Unlike functions, tasks are never synthesizable and can consume simulation time.
 *
 *  ```
 *  @Task
 *  fun f() {
 *      delay(100)
 *      print("abc")
 *  }
 *  ```
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Task
