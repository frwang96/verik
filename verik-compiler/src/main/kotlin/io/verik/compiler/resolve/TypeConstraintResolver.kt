/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.resolve

import io.verik.compiler.ast.common.Type
import io.verik.compiler.core.common.Cardinal
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.Messages

/**
 * Resolver for resolving type constraints.
 */
object TypeConstraintResolver {

    fun resolve(typeConstraint: TypeConstraint): Boolean {
        return when (typeConstraint.kind) {
            TypeConstraintKind.EQ_IN ->
                resolveTypeConstraintEqIn(typeConstraint.typeAdapters)
            TypeConstraintKind.EQ_OUT ->
                resolveTypeConstraintEqOut(typeConstraint.typeAdapters)
            TypeConstraintKind.EQ_INOUT ->
                resolveTypeConstraintEqInout(typeConstraint.typeAdapters)
            TypeConstraintKind.LOG_IN ->
                resolveTypeConstraintLogIn(typeConstraint.typeAdapters)
            TypeConstraintKind.WIDTH_OUT ->
                resolveTypeConstraintWidthOut(typeConstraint.typeAdapters)
            TypeConstraintKind.MAX_OUT ->
                resolveTypeConstraintMaxAdd(typeConstraint.typeAdapters, TypeConstraintKind.MAX_OUT)
            TypeConstraintKind.MAX_INC_OUT ->
                resolveTypeConstraintMaxAdd(typeConstraint.typeAdapters, TypeConstraintKind.MAX_INC_OUT)
            TypeConstraintKind.ADD_OUT ->
                resolveTypeConstraintMaxAdd(typeConstraint.typeAdapters, TypeConstraintKind.ADD_OUT)
            TypeConstraintKind.CAT_OUT ->
                resolveTypeConstraintCatOut(typeConstraint.typeAdapters)
            TypeConstraintKind.REP_OUT ->
                resolveTypeConstraintRepOut(typeConstraint.typeAdapters)
            TypeConstraintKind.EXT_IN ->
                resolveTypeConstraintExtTru(typeConstraint.typeAdapters, true)
            TypeConstraintKind.TRU_IN ->
                resolveTypeConstraintExtTru(typeConstraint.typeAdapters, false)
        }
    }

    private fun resolveTypeConstraintEqIn(typeAdapters: List<TypeAdapter>): Boolean {
        val outerType = typeAdapters[1].getType()
        if (!outerType.isResolved())
            return false
        setType(typeAdapters, outerType, false)
        return true
    }

    private fun resolveTypeConstraintEqOut(typeAdapters: List<TypeAdapter>): Boolean {
        val innerType = typeAdapters[1].getType()
        if (!innerType.isResolved())
            return false
        setType(typeAdapters, innerType, true)
        return true
    }

    private fun resolveTypeConstraintEqInout(typeAdapters: List<TypeAdapter>): Boolean {
        val innerType = typeAdapters[1].getType()
        val outerType = typeAdapters[0].getType()
        return when {
            innerType.isResolved() -> {
                setType(typeAdapters, innerType, true)
                true
            }
            outerType.isResolved() -> {
                typeAdapters[1].setType(outerType.copy())
                true
            }
            else -> false
        }
    }

    private fun resolveTypeConstraintLogIn(typeAdapters: List<TypeAdapter>): Boolean {
        val outerType = typeAdapters[1].getType()
        if (!outerType.isResolved())
            return false
        val outerValue = outerType.asCardinalValue(typeAdapters[1].getElement())
        val expectedInnerValue = if (outerValue <= 0) 0 else (32 - (outerValue - 1).countLeadingZeroBits())
        val expectedInnerType = Cardinal.of(expectedInnerValue).toType()
        setType(typeAdapters, expectedInnerType, false)
        return true
    }

    private fun resolveTypeConstraintWidthOut(typeAdapters: List<TypeAdapter>): Boolean {
        val innerType = typeAdapters[1].getType()
        if (!innerType.isResolved())
            return false
        val innerValue = innerType.asCardinalValue(typeAdapters[1].getElement())
        val actualOuterValue = if (innerValue < 0) 0 else (32 - innerValue.countLeadingZeroBits())
        val actualOuterType = Cardinal.of(actualOuterValue).toType()
        setType(typeAdapters, actualOuterType, true)
        return true
    }

    private fun resolveTypeConstraintMaxAdd(
        typeAdapters: List<TypeAdapter>,
        typeConstraintKind: TypeConstraintKind
    ): Boolean {
        val innerTypes = typeAdapters.drop(1).map { it.getType() }
        if (innerTypes.any { !it.isResolved() })
            return false
        val innerValues = typeAdapters.drop(1).map { it.getType().asCardinalValue(it.getElement()) }
        val actualOuterValue = when (typeConstraintKind) {
            TypeConstraintKind.MAX_OUT -> innerValues.maxOrNull() ?: 0
            TypeConstraintKind.MAX_INC_OUT -> (innerValues.maxOrNull() ?: 0) + 1
            TypeConstraintKind.ADD_OUT -> innerValues.sum()
            else -> Messages.INTERNAL_ERROR.on(typeAdapters[0].getElement(), "Unexpected type constraint kind")
        }
        val actualOuterType = Cardinal.of(actualOuterValue).toType()
        setType(typeAdapters, actualOuterType, true)
        return true
    }

    private fun resolveTypeConstraintCatOut(typeAdapters: List<TypeAdapter>): Boolean {
        val innerTypes = typeAdapters.drop(1).map { it.getType() }
        if (innerTypes.any { !it.isResolved() })
            return false
        val innerValues = typeAdapters.drop(1).map { it.getType().getWidthAsInt(it.getElement()) }
        val actualOuterValue = innerValues.sum()
        val actualOuterType = Cardinal.of(actualOuterValue).toType()
        setType(typeAdapters, actualOuterType, true)
        return true
    }

    private fun resolveTypeConstraintRepOut(typeAdapters: List<TypeAdapter>): Boolean {
        val replicationCountType = typeAdapters[1].getType()
        val replicationValueType = typeAdapters[2].getType()
        if (!replicationCountType.isResolved() || !replicationValueType.isResolved())
            return false
        val replicationCountValue = replicationCountType.asCardinalValue(typeAdapters[1].getElement())
        val replicationValueWidth = replicationValueType.getWidthAsInt(typeAdapters[2].getElement())
        val actualOuterValue = replicationCountValue * replicationValueWidth
        val actualOuterType = Cardinal.of(actualOuterValue).toType()
        setType(typeAdapters, actualOuterType, true)
        return true
    }

    private fun resolveTypeConstraintExtTru(typeAdapters: List<TypeAdapter>, isExt: Boolean): Boolean {
        val innerType = typeAdapters[0].getType()
        val outerType = typeAdapters[1].getType()
        if (!innerType.isResolved() || !outerType.isResolved())
            return false
        val innerValue = innerType.asCardinalValue(typeAdapters[0].getElement())
        val outerValue = outerType.asCardinalValue(typeAdapters[1].getElement())
        if (isExt) {
            if (innerValue > outerValue) {
                Messages.EXTENSION_ERROR.on(
                    typeAdapters[0].getElement(),
                    typeAdapters[0].getFullType(),
                    typeAdapters[0].substituteFullType(outerType)
                )
            }
        } else {
            if (innerValue < outerValue) {
                Messages.TRUNCATION_ERROR.on(
                    typeAdapters[0].getElement(),
                    typeAdapters[0].getFullType(),
                    typeAdapters[0].substituteFullType(outerType)
                )
            }
        }
        return true
    }

    private fun setType(typeAdapters: List<TypeAdapter>, type: Type, isActual: Boolean) {
        val typeAdapterType = typeAdapters[0].getType()
        if (isIgnored(type) || isIgnored(typeAdapterType))
            return
        // TODO property handle type variance
        if (typeAdapterType.isResolved()) {
            val typeAdapterSuperTypes = typeAdapterType.getSuperTypes()
            if (hasCardinalType(type) && type !in typeAdapterSuperTypes) {
                if (isActual) {
                    Messages.MISMATCHED_TYPE.on(
                        typeAdapters[0].getElement(),
                        typeAdapters[0].getFullType(),
                        typeAdapters[0].substituteFullType(type)
                    )
                } else {
                    Messages.MISMATCHED_TYPE.on(
                        typeAdapters[0].getElement(),
                        typeAdapters[0].substituteFullType(type),
                        typeAdapters[0].getFullType()
                    )
                }
            }
        } else {
            typeAdapters[0].setType(type.copy())
        }
    }

    private fun isIgnored(type: Type): Boolean {
        return type.reference in listOf(Core.Kt.C_Function, Core.Kt.C_Nothing)
    }

    private fun hasCardinalType(type: Type): Boolean {
        return if (type.isCardinalType()) {
            true
        } else {
            type.arguments.any { hasCardinalType(it) }
        }
    }
}
