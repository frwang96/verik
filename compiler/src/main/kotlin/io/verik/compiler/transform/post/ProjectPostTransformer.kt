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

package io.verik.compiler.transform.post

import io.verik.compiler.common.ProjectPass
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m

object ProjectPostTransformer : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        m.log("PostTransform: Transform loop expressions")
        LoopExpressionTransformer.pass(projectContext)
        m.flush()

        m.log("PostTransform: Transform function references")
        FunctionReferenceTransformer.pass(projectContext)
        m.flush()

        m.log("PostTransform: Transform special functions")
        FunctionSpecialTransformer.pass(projectContext)
        m.flush()

        m.log("PostTransform: Transform unary expressions")
        UnaryExpressionTransformer.pass(projectContext)
        m.flush()

        m.log("PostTransform: Transform binary expressions")
        BinaryExpressionTransformer.pass(projectContext)
        m.flush()

        m.log("PostTransform: Transform package names")
        PackageNameTransformer.pass(projectContext)
        m.flush()

        m.log("PostTransform: Transform reference expressions")
        ReferenceExpressionTransformer.pass(projectContext)
        m.flush()

        m.log("PostTransform: Transform block expressions")
        BlockExpressionTransformer.pass(projectContext)
        m.flush()

        m.log("PostTransform: Insert parenthesis")
        ParenthesisInsertionTransformer.pass(projectContext)
        m.flush()
    }
}