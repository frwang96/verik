package com.verik.core

import org.antlr.v4.runtime.misc.ParseCancellationException

// Copyright (c) 2020 Francis Wang

class AntlrTreeParseException(val line: Int, val pos: Int, msg: String): ParseCancellationException(msg)
