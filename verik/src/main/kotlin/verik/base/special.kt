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
 * The type with only one value, the [_unit] object.
 */
typealias _unit = Unit

/**
 * The base type for all classes.
 */
typealias _any = Any

/**
 * (UNIMPLEMENTED) Returns an instance of [x] with all fields set to unknown.
 */
@Suppress("FunctionName")
fun <T: _data> X(x: T): T {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Returns an instance of [x] with all fields set to unknown.
 */
@Suppress("FunctionName")
fun X(x: _bool): _bool {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Returns an instance of [x] with all fields set to floating.
 */
@Suppress("FunctionName")
fun <T: _data> Z(x: T): T {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Returns an instance of [x] with all fields set to floating.
 */
@Suppress("FunctionName")
fun Z(x: _bool): _bool {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Returns a null object of type [x].
 */
@Suppress("FunctionName")
fun <T: _class> NULL(x: T): T {
    throw VerikDslException()
}

/**
 * (UNIMPLEMENTED) Explicitly label the type of a function declaration.
 */
fun type(x: _any, vararg y: _any) {
    throw VerikDslException()
}