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

@file:Suppress("UNUSED_PARAMETER", "unused")

package verik.base

/**
 * A bus that carries signals between [modules][Module]. Buses can contain [bus ports][BusPort] and
 * [clock ports][ClockPort] to control signal directions and timing. They correspond to SystemVerilog interfaces.
 *
 *      class B: Bus() {
 *
 *          @input val x = t_Boolean()
 *      }
 */
abstract class Bus: Component()