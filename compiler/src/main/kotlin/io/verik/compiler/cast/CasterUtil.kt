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

package io.verik.compiler.cast

import io.verik.compiler.ast.common.QualifiedName
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.descriptor.ClassDescriptor
import io.verik.compiler.core.CoreClass
import io.verik.compiler.main.MessageLocation
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.PsiDiagnosticUtils
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.getImmediateSuperclassNotAny
import java.nio.file.Paths

object CasterUtil {

    fun getMessageLocation(element: PsiElement): MessageLocation {
        val lineAndColumn = PsiDiagnosticUtils.offsetToLineAndColumn(
            element.containingFile.viewProvider.document,
            element.textRange.startOffset
        )
        val path = Paths.get(element.containingFile.virtualFile.path)
        return MessageLocation(lineAndColumn.column, lineAndColumn.line, path)
    }

    fun getType(type: KotlinType): Type {
        val qualifiedName = QualifiedName(type.getJetTypeFqName(false))
        val name = qualifiedName.toName()
        val supertype = type.getImmediateSuperclassNotAny().let {
            if (it != null) getType(it)
            else CoreClass.ANY.getDefaultType()
        }
        val classDescriptor = ClassDescriptor(name, qualifiedName, supertype.classDescriptor)
        return Type(classDescriptor)
    }
}