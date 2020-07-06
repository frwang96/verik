# Verik Overview

## Context

`@top` : Annotation for top-level module

`@extern` : Annotation for externally sourced module

## Data Types

`val type = _type(n, m, ...)` : Declare data net with parameters

`type put <expr>` : Set value of net combinationally

`type reg <expr>` : Set value of net sequentially

`type drive <expr>` : Drive value of net with a tri-state buffer

`type con <expr>` : Connect net during component instantiation

### Native

`_bool` : Boolean value of either `true` or `false`

`_sint(n)` : Signed integer of n bits

`_uint(n)` : Unsigned integer of n bits

`_array(n, _type())` : Array of length n

Operators on native types are defined in [operators](res/operators.md)

### Enums

```
enum class _op(val rep: _uint = _uint(values().size)): _enum {
    ADD, SUB, MUL, RST
}
```

### Structs

```
class _req: _struct {
    val addr = _uint(2)
    val data = _uint(8)
}
```

## Components

`val _type = _type(n, m, ...) with { ... }` : Declare component with parameters and connections

### Class

`val type = _type(n, m, ...)` : Declare variable with parameters

`val type = type(n, m, ...)` : Initialize variable with parameters

`type.is_null()` : Check nullity

## Nets

### Port Type

`@input` : Input of module

`@output` : Output of module

`@inoutput` : Bidirectional port of module

`@intf` : Interface of module

`@port` : Interface port of module

## Control Flow

### Put
```
@put fun f() {
    x put true
}
```

### Reg
```
@reg fun f() {
    x reg true
}
```

### Drive
```
@drive fun f() {
    x drive if (y) true else null
}
```

### Initial
```
@initial fun f() {
    x put true
}
```
