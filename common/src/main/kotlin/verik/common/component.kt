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

@file:Suppress("UNUSED_PARAMETER")

package verik.common

interface _component

infix fun <TYPE: _component> TYPE.with(block: (TYPE) -> Unit): TYPE {
    throw VerikDslException()
}

interface _module: _component

// infix fun _interf.put(x: _interf) {}
// infix fun _interf.con(x: _interf) {}
interface _interf: _component

infix fun _interf.put(x: _x) {
    throw VerikDslException()
}

infix fun _interf.put(x: _z) {
    throw VerikDslException()
}

// infix fun _modport.con(x: _modport) {}
interface _modport: _component
