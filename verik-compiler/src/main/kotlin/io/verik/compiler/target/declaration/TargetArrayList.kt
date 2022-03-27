/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.target.declaration

import io.verik.compiler.target.common.CompositeTargetFunctionDeclaration
import io.verik.compiler.target.common.Target
import io.verik.compiler.target.common.TargetScope

object TargetArrayList : TargetScope(Target.C_ArrayList) {

    val F_new = CompositeTargetFunctionDeclaration(
        parent,
        "__new",
        """
            static function automatic ArrayList#(E) __new();
                ArrayList#(E) arrayList = new();
                return arrayList;
            endfunction : __new
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
