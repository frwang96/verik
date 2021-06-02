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
 * Annotates component instantiations.
 */
annotation class ins

/**
 * Annotates the top module of the hierarchy.
 */
annotation class top

/**
 * Annotates input ports of a component.
 */
annotation class input

/**
 * Annotates output ports of a component.
 */
annotation class output

/**
 * Annotates bidirectional ports of a component.
 */
annotation class bidir

/**
 * Annotates combinational action blocks. Combinational action blocks are executed when the value of its contents
 * change and are used to model combinational logic. They correspond to SystemVerilog always_comb blocks.
 *
 *      @com fun f() {
 *          ...
 *      }
 */
annotation class com

/**
 * Annotates sequential action blocks. Sequential action blocks are executed when its event expression is triggered,
 * and are used to model sequential logic. Sequential action blocks must contain an [on] expression to specify its
 * event expression. They correspond to SystemVerilog always_ff blocks.
 *
 *      @seq fun f() {
 *          on (posedge(clk)) {
 *              ...
 *          }
 *      }
 */
annotation class seq

/**
 * Annotates run action blocks. Run action blocks are started at the beginning of the simulation and are executed only
 * once during the entire simulation. They correspond to SystemVerilog initial blocks.
 *
 *
 *      @run fun f() {
 *          ...
 *      }
 */
annotation class run

/**
 * Annotates tasks. Unlike functions, tasks can consume simulation time.
 *
 *      @task fun f() {
 *          ...
 *      }
 */
annotation class task