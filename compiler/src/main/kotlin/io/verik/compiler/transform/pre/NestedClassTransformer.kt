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

package io.verik.compiler.transform.pre

import io.verik.compiler.ast.common.Name
import io.verik.compiler.ast.common.TreeVisitor
import io.verik.compiler.ast.element.VkBaseClass
import io.verik.compiler.ast.element.VkFile
import io.verik.compiler.ast.element.cast
import io.verik.compiler.main.ProjectContext

object NestedClassTransformer {

    fun transform(projectContext: ProjectContext) {
        val nestedClasses = ArrayList<VkBaseClass>()
        val nestedClassVisitor = NestedClassVisitor(nestedClasses)
        projectContext.vkFiles.forEach {
            it.accept(nestedClassVisitor)
        }
        nestedClasses.forEach { unnestNestedClass(it) }
    }

    private fun unnestNestedClass(baseClass: VkBaseClass) {
        val parentClass = baseClass.parent.cast<VkBaseClass>(baseClass)
        if (parentClass != null) {
            val name = Name(parentClass.name.name + "\$" + baseClass.name.name)
            baseClass.name = name
            parentClass.removeChild(baseClass)
            parentClass.insertSibling(baseClass)
        }
    }

    class NestedClassVisitor(private val nestedClasses: ArrayList<VkBaseClass>): TreeVisitor() {

        override fun visitBaseClass(baseClass: VkBaseClass) {
            if (baseClass.parent !is VkFile)
                nestedClasses.add(baseClass)
            super.visitBaseClass(baseClass)
        }
    }
}