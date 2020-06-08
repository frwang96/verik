# Verik Guide
## Annotations

### Context

##### `@Virtual`

- Component is expected at simulation time so sources are not transpiled

### Components

#### Circuit

#### Module

#### Interface

#### Port

#### Class

### IO

#### `@In`

#### `@Out`

#### `@InOut`

### Net Type

#### `@Wire`

#### `@Reg`

# Data Types

## Operations

`val type = Type()`: Construct type

`val type = Type() array "8"`: Construct array of type

`type[0]`: Index element of type array

`type[4:0]`: Index range of type array

`type con <expr>`: Connect combinationally

`type set <expr>`: Set sequentially

## Control Flow

### Initial

### Always

## Verik Commands

### Control Flow

### Logging