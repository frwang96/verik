/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused")

package io.verik.core

/**
 * Module that may be synthesized to hardware. They correspond to SystemVerilog modules.
 *
 *  ```
 *  class M : Module() {
 *
 *      var clk = false
 *
 *      @Run
 *      fun runClk() {
 *          forever {
 *              delay(1)
 *              clk = !clk
 *          }
 *      }
 *  }
 *  ```
 */
abstract class Module : Component()
