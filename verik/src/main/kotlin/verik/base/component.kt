/*
 * Copyright 2020 Francis Wang
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

import verik.data.*

/**
 * Component that can be synthesized to hardware.
 */
abstract class _component {

    /**
     * (UNIMPLEMENTED) Returns a string representation of the component.
     */
    fun to_string(): _string {
        throw VerikDslException()
    }
}

/**
 * Instantiate component with connections in [block].
 */
infix fun <TYPE: _component> TYPE.with(block: (TYPE) -> _unit): TYPE {
    throw VerikDslException()
}

/**
 * Module that can be synthesized to hardware.
 */
abstract class _module: _component()

/**
 * (UNIMPLEMENTED) Bus that carries signals between [modules][_module]. The following functions are automatically
 * generated.
 *
 *      infix fun _bus.con(x: _bus): _unit
 *      infix fun _bus.set(x: _bus): _unit
 */
abstract class _bus: _component()

/**
 * (UNIMPLEMENTED) Bus port to bundle ports in [busses][_bus]. The following functions are automatically generated.
 *
 *      infix fun _busport.con(x: _busport): _unit
 */
abstract class _busport: _component()
