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

package io.verik.compiler.target.declaration

import io.verik.compiler.target.common.CompositeTargetFunctionDeclaration

object TargetArrayList {

    val F_new = CompositeTargetFunctionDeclaration(
        "_${'$'}new",
        """
            static function automatic ArrayList _${'$'}new();
                automatic ArrayList arrayList = new();
                return arrayList;
            endfunction : _${'$'}new
        """.trimIndent()
    )

    val F_add = CompositeTargetFunctionDeclaration(
        "add",
        """
            function automatic add(E e);
                queue.push_back(e);
            endfunction : add
        """.trimIndent()
    )
}
