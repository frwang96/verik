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

package verik.core.it

import verik.core.main.Line
import verik.core.main.LineException
import verik.core.vkx.VkxOperatorIdentifier

enum class ItOperatorIdentifier {
    ADD,
    SUB,
    MUL;

    companion object {

        operator fun invoke(identifier: VkxOperatorIdentifier, line: Line): ItOperatorIdentifier {
            return when (identifier) {
                VkxOperatorIdentifier.ADD -> ADD
                VkxOperatorIdentifier.SUB -> SUB
                VkxOperatorIdentifier.MUL -> MUL
                else -> throw LineException("operator identifier not supported", line)
            }
        }
    }
}
