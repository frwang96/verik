package com.verik.core.kt

import com.verik.core.LinePos
import org.antlr.v4.runtime.misc.ParseCancellationException

// Copyright (c) 2020 Francis Wang

class KtAntlrException(val linePos: LinePos, msg: String): ParseCancellationException(msg)
class KtParseException(val linePos: LinePos, msg: String): Exception(msg)