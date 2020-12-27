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

package verikc.al.ast

import verikc.base.ast.Line
import verikc.base.ast.LineException

data class AlTree(
    val line: Line,
    val index: Int,
    val text: String,
    val children: List<AlTree>
) {

    fun contains(index: Int): Boolean {
        return children.any { it.index == index }
    }

    fun find(index: Int): AlTree {
        var match: AlTree? = null
        children.forEach {
            if (it.index == index) {
                if (match == null) {
                    match = it
                } else {
                    throw LineException("syntax tree has multiple matching children", line)
                }
            }
        }
        return match ?: throw LineException("syntax tree has no matching children", line)
    }

    fun findAll(index: Int): List<AlTree> {
        val matches = ArrayList<AlTree>()
        children.forEach {
            if (it.index == index) {
                matches.add(it)
            }
        }
        return matches
    }

    fun unwrap(): AlTree {
        if (children.size != 1) throw LineException("could not unwrap syntax tree", line)
        return children[0]
    }
}