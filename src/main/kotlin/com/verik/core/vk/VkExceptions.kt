package com.verik.core.vk

import com.verik.core.LinePos

// Copyright (c) 2020 Francis Wang

class VkParseException(val linePos: LinePos, msg: String): Exception(msg)