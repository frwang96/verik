/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.core

/**
 * A module port to bundle signals in [module interfaces][ModuleInterface] and assign directionality. They correspond
 * to SystemVerilog modports.
 *
 *  ```
 *  class MP(
 *      @In var x: Boolean
 *  ) : ModulePort()
 *
 *  class MI : ModuleInterface() {
 *
 *      var x: Boolean = nc()
 *
 *      @Make
 *      val mp = MP(x)
 *  }
 *  ```
 */
abstract class ModulePort : Component()
