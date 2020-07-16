package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.KtRule
import com.verik.core.sv.SvExpression
import com.verik.core.sv.SvStatement

// Copyright (c) 2020 Francis Wang

sealed class VkExpression(
        open var dataType: VkDataType,
        open val linePos: LinePos) {

    fun extractStatement(): SvStatement {
        return VkStatementExtractor.extractStatement(this)
    }

    fun extractExpression(): SvExpression {
        return VkExpressionExtractor.extractExpression(this)
    }

    companion object {

        operator fun invoke(expression: KtRule): VkExpression {
            return VkExpressionParser.parse(expression)
        }
    }
}

data class VkLambdaExpression(
        override var dataType: VkDataType,
        override val linePos: LinePos,
        val statements: List<VkStatement>
): VkExpression(dataType, linePos) {

    constructor(linePos: LinePos, statements: List<VkStatement>): this(VkUnitType, linePos, statements)
}

data class VkCallableExpression(
        override var dataType: VkDataType,
        override val linePos: LinePos,
        val target: VkExpression,
        val args: List<VkExpression>
): VkExpression(dataType, linePos) {

    constructor(linePos: LinePos, target: VkExpression, args: List<VkExpression>):
            this(VkUnitType, linePos, target, args)
}

data class VkOperatorExpression(
        override var dataType: VkDataType,
        override val linePos: LinePos,
        val type: VkOperatorType,
        val args: List<VkExpression>
): VkExpression(dataType, linePos) {

    constructor(linePos: LinePos, type: VkOperatorType, args: List<VkExpression>):
            this(VkUnitType, linePos, type, args)
}

data class VkNavigationExpression(
        override var dataType: VkDataType,
        override val linePos: LinePos,
        val target: VkExpression,
        val identifier: String
): VkExpression(dataType, linePos) {

    constructor(linePos: LinePos, target: VkExpression, identifier: String):
            this(VkUnitType, linePos, target, identifier)
}

data class VkIdentifierExpression(
        override var dataType: VkDataType,
        override val linePos: LinePos,
        val identifier: String
): VkExpression(dataType, linePos) {

    constructor(linePos: LinePos, identifier: String): this(VkUnitType, linePos, identifier)
}

data class VkLiteralExpression(
        override var dataType: VkDataType,
        override val linePos: LinePos,
        val value: String
): VkExpression(dataType, linePos) {

    constructor(linePos: LinePos, value: String): this(VkUnitType, linePos, value)
}

data class VkStringExpression(
        override var dataType: VkDataType,
        override val linePos: LinePos,
        val segments: List<VkStringSegment>
): VkExpression(dataType, linePos) {

    constructor(linePos: LinePos, segments: List<VkStringSegment>): this(VkUnitType, linePos, segments)
}
