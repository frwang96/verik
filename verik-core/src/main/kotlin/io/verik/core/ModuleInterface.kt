/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused")

package io.verik.core

/**
 * A module interface that carries signals between [modules][Module]. Module interfaces can contain
 * [module ports][ModulePort] to control signal direction. They correspond to SystemVerilog interfaces.
 *
 *  ```
 *  class MI(
 *      @In var clk: Boolean
 *  ) : ModuleInterface() {
 *
 *      var x: Boolean = nc()
 *  }
 *  ```
 */
abstract class ModuleInterface : Component()
