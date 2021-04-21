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
 * A simulation event.
 */
class Event: Instance()

/**
 * Returns an event for a positive edge transition.
 * @param x the value to trigger on
 * @return an event for the positive edge transition of [x]
 */
fun posedge(x: Boolean): Event {
    throw VerikException()
}

/**
 * Returns an event for a negative edge transition.
 * @param x the value to trigger on
 * @return an event for the negative edge transition of [x]
 */
fun negedge(x: Boolean): Event {
    throw VerikException()
}
