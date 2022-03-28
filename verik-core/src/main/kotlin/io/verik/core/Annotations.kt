/*
 * SPDX-License-Identifier: Apache-2.0
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
 * Annotates that a declaration is an entry point for compilation. By default, all entry points are elaborated. If dead
 * code elimination is enabled, declarations that are not reachable from any entry point are eliminated.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class Entry

/**
 * Inject a string literal directly as SystemVerilog.
 *
 *  ```
 *  class C : Class() {
 *
 *      @Inj
 *      val macro = "`macro(C);"
 *  }
 *  ```
 */
@Target(AnnotationTarget.PROPERTY)
annotation class Inj

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
 * Annotates wire ports of a component. Wire ports are bidirectional and must be declared as var.
 *
 *  ```
 *  class M(@Wire var x: Boolean) : Module()
 *  ```
 */
@Target(AnnotationTarget.PROPERTY)
annotation class Wire

/**
 * Annotates that a property in a [Class] should be randomized.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class Rand

/**
 * Annotates that a property in a [Class] should be randomized cyclically.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class Randc

/**
 * Annotates randomization constraints in a [Class].
 */
@Target(AnnotationTarget.PROPERTY)
annotation class Cons

/**
 * Annotates assertion properties.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class Assert

/**
 * Annotates cover properties, cover points, and cover crosses.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class Cover

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
