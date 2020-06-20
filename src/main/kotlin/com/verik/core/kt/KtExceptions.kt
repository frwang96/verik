package com.verik.core.kt

import org.antlr.v4.runtime.misc.ParseCancellationException

// Copyright (c) 2020 Francis Wang

class KtAntlrException(val line: Int, val pos: Int, msg: String): ParseCancellationException(msg)
class KtParseException(val line: Int, val pos: Int, msg: String): Exception(msg)