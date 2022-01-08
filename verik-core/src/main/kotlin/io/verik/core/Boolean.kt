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

@file:Suppress("unused")

package io.verik.core

/**
 * (UNIMPLEMENTED) Returns true if the boolean is unknown.
 */
fun Boolean.isUnknown(): Boolean {
    throw VerikException()
}

/**
 * Extend to [X] bits with zero extension.
 */
fun <X : `*`> Boolean.ext(): Ubit<X> {
    throw VerikException()
}

/**
 * Extend to [X] bits with sign extension.
 */
fun <X : `*`> Boolean.sext(): Sbit<X> {
    throw VerikException()
}
