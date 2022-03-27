/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UNUSED_PARAMETER")

package io.verik.core

/**
 * A clocking block to bundle signals synchronized on an event. Clocking blocks can be instantiated in
 * [modules][Module] and [module interfaces][ModuleInterface]. They correspond to SystemVerilog clocking blocks.
 *
 *  ```
 *  class CB(
 *      override val event: Event,
 *      @In var x: Boolean
 *  ) : ClockingBlock()
 *
 *  class M : Module() {
 *
 *      var clk: Boolean = nc()
 *      var x: Boolean = nc()
 *
 *      @Make
 *      val cb = CB(posedge(clk), x)
 *  }
 *  ```
 */
abstract class ClockingBlock : Component() {

    /**
     * Event that triggers the clocking block.
     */
    abstract val event: Event
}
