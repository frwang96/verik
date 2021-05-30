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

@file:Suppress("UNUSED_PARAMETER")

package io.verik.core

/**
 * A clock port to bundle signals synchronized on a clock edge. Clock ports can be instantiated in [modules][Module] and
 * [buses][Bus]. They correspond to SystemVerilog clocking blocks.
 *
 *      class CP(
 *          event: Event,
 *          @input var x: Boolean
 *      ): ClockPort(event)
 *
 *      class M: Module() {
 *
 *          var clk: Boolean = x()
 *          var x: Boolean = x()
 *
 *          @ins cp = CP(
 *              event = posedge(clk),
 *              x     = x
 *          )
 *      }
 */
abstract class ClockPort(event: Event)
