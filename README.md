# Verik Guide

## Context

`@Main` : Main module for simulation

`@Virtual` : Component is expected at simulation time so sources are not transpiled

## Data Types

`val type = Type(n, m, ...)` : Construct type with type parameters

`type set <expr>` : Set value of type net combinationally or sequentially

`type con <expr>` : Connect type net during module instantiation

### Native

`Bool` : Boolean value of either `true` or `false`

`Bits(n)` : Group of n bits

`SNum(n)` :   Signed number of n bits

`UNum(n)` : Unsigned number of n bits

Operators on native types are defined in [operators](operators.md)

### Enums

```
enum class AluOp(val bits: Bits): Data {
        no_op  (Bits.of("2b'00")),
        add_op (Bits.of("2b'01")),
        sub_op (Bits.of("2b'10")),
        mul_op (Bits.of("2b'11")),
        companion object {operator fun invoke() = values()[0]}
}
```

### Structs

```
class Req: Data {
    val addr = UNum(4)
    val data = UNum(8)
}
```

### Compound

`Vector(n, Data())` : Vector of data of length n

`EnumSet(Enum())` : One hot encoding of enum

## Components

### Module

### Circuit

### Interface

### Class

## Nets

### IO Type

`@In var Data() ` : Input to module

`@Out var Data()` : Output from module

`@Port var Interface()` : Interface or intport connection to module

## Routines

### Tasks

### Functions

## Control Flow

### Seq

### Comb

### Initial

## Verik Commands

### Control Flow

### Logging