/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.common

import io.verik.compiler.ast.common.Declaration
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.impl.AbstractTypeAliasDescriptor
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

object CoreDeclarationMap {

    private val qualifiedSignatureMap = HashMap<QualifiedSignature, Declaration>()

    init {
        addCoreDeclarations(Core::class)
        add(QualifiedSignature("${CorePackage.VK.name}.Cardinal", "class Cardinal"), Cardinal.UNRESOLVED)
        add(QualifiedSignature("${CorePackage.VK.name}.*", "typealias *"), Cardinal.UNRESOLVED)
    }

    private fun addCoreDeclarations(kClass: KClass<*>) {
        val kClassInstance = kClass.objectInstance!!
        kClass.declaredMemberProperties.forEach {
            @Suppress("UNCHECKED_CAST")
            val coreDeclaration = (it as KProperty1<Any, *>).get(kClassInstance) as CoreDeclaration
            add(coreDeclaration.getQualifiedSignature(), coreDeclaration)
        }
        kClass.nestedClasses.forEach { addCoreDeclarations(it) }
    }

    private fun add(qualifiedSignature: QualifiedSignature, declaration: Declaration) {
        if (qualifiedSignature in qualifiedSignatureMap)
            throw IllegalArgumentException("Repeated declaration signature: ${qualifiedSignature.signature}")
        qualifiedSignatureMap[qualifiedSignature] = declaration
    }

    operator fun get(declarationDescriptor: DeclarationDescriptor): Declaration? {
        val qualifiedSignature = QualifiedSignature.get(declarationDescriptor)
        val coreDeclaration = qualifiedSignatureMap[qualifiedSignature]
        return when {
            coreDeclaration != null -> coreDeclaration
            declarationDescriptor is AbstractTypeAliasDescriptor -> {
                val name = declarationDescriptor.name.asString()
                val cardinal = name.toIntOrNull()
                if (cardinal != null) Cardinal.of(cardinal)
                else null
            }
            else -> null
        }
    }
}
