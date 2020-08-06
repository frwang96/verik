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

package io.verik.common

import io.verik.assert.assertThrowsMessage
import org.junit.jupiter.api.Test

internal class ExceptionsTest {

    @Test
    fun `edge throws exception`() {
        assertThrowsMessage<VerikDslException>("function is part of the verik dsl and should not be used directly") {
            posedge(false)
        }
        assertThrowsMessage<VerikDslException>("function is part of the verik dsl and should not be used directly") {
            negedge(false)
        }
    }

    @Test
    fun `control throws exception`() {
        assertThrowsMessage<VerikDslException>("function is part of the verik dsl and should not be used directly") {
            forever {}
        }
        assertThrowsMessage<VerikDslException>("function is part of the verik dsl and should not be used directly") {
            repeat(1) {}
        }
        assertThrowsMessage<VerikDslException>("function is part of the verik dsl and should not be used directly") {
            wait()
        }
    }
}