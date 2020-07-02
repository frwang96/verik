package com.verik.core.vk

// Copyright (c) 2020 Francis Wang

enum class VkFunctionType {
    REGULAR,
    TARGETED,
    OPERATOR
}

sealed class VkExpressionBody
data class VkFunctionExpression(val name: String, val functionType: VkFunctionType, val args: List<VkExpression>): VkExpressionBody()
data class VkNavigationExpression(val target: VkExpression, val identifier: String): VkExpressionBody()
data class VkIdentifierExpression(val identifier: String): VkExpressionBody()
data class VkLiteralExpression(val value: String): VkExpressionBody()
