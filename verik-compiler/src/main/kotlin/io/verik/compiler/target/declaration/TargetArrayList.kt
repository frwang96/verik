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
import io.verik.compiler.target.common.Target
import io.verik.compiler.target.common.TargetScope

object TargetArrayList : TargetScope(Target.C_ArrayList) {

    val F_new = CompositeTargetFunctionDeclaration(
        parent,
        "_${'$'}new",
        """
            static function automatic ArrayList#(E) _${'$'}new();
                automatic ArrayList#(E) arrayList = new();
                return arrayList;
            endfunction : _${'$'}new
        """.trimIndent(),
        true
    )

    val F_add = CompositeTargetFunctionDeclaration(
        parent,
        "add",
        """
            function automatic void add(E e);
                queue.push_back(e);
            endfunction : add
        """.trimIndent(),
        false
    )

    val F_get = CompositeTargetFunctionDeclaration(
        parent,
        "get",
        """
            function automatic E get(int index);
                return queue[index];
            endfunction : get
        """.trimIndent(),
        false
    )

    val F_set = CompositeTargetFunctionDeclaration(
        parent,
        "set",
        """
            function automatic set(int index, E e);
                queue[index] = e;
            endfunction : set
        """.trimIndent(),
        false
    )

    val F_size = CompositeTargetFunctionDeclaration(
        parent,
        "size",
        """
            function automatic int size();
                return queue.size();
            endfunction : size
        """.trimIndent(),
        false
    )
}
