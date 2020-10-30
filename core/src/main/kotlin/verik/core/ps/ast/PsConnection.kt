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

package verik.core.ps.ast

import verik.core.base.ast.Line
import verik.core.base.ast.Symbol
import verik.core.rf.ast.RfConnection

data class PsConnection(
        override val line: Int,
        val port: Symbol,
        val connection: Symbol
): Line {

    constructor(connection: RfConnection): this(
            connection.line,
            connection.port,
            connection.connection
    )
}