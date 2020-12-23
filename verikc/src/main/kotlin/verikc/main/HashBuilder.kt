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

package verikc.main

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

object HashBuilder {

    fun build(string: String): String {
        val messageDigest = MessageDigest.getInstance("MD5")
        messageDigest.digest(string.toByteArray(StandardCharsets.UTF_8))
        val byteArray = messageDigest.digest()
        val charArray = CharArray(32)
        for (i in 0 until 16) {
            val x = byteArray[i].toInt() and 0xff
            charArray[i * 2] = getHexChar(x shr 4)
            charArray[i * 2 + 1] = getHexChar(x and 0x0f)
        }
        return String(charArray)
    }

    private fun getHexChar(x: Int): Char {
        return when {
            x < 10 -> '0' + x
            x < 16 -> 'a' + x - 10
            else -> throw IllegalArgumentException("hex char out of range")
        }
    }
}