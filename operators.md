# Operators

## Native

### `a && b`, `a || b`
```
bool bool -> bool
```

### `+a`, `-a`
```
sint(n) -> sint(n)
uint(n) -> uint(n)
```

### `!a`
```
bool    -> bool
sint(n) -> bool
uint(n) -> bool
```

### `a + b`, `a - b`, `a * b`
```
Int sint(n)     -> sint(n)
Int uint(n)     -> uint(n)
sint(n) Int     -> sint(n)
sint(n) sint(m) -> sint(max(n, m))
sint(n) uint(m) -> uint(max(n, m))
uint(n) Int     -> uint(n)
uint(n) sint(m) -> uint(max(n, m))
uint(n) uint(m) -> uint(max(n, m))
```

### `a > b`, `a >= b`, `a < b`, `a <= b`
```
Int sint(n)     -> bool
Int uint(n)     -> bool
sint(n) Int     -> bool
sint(n) sint(n) -> bool
uint(n) Int     -> bool
uint(n) uint(n) -> bool
```

### `a[p]`
```
sint(n) -> bool
uint(n) -> bool
```

### `a[p, q]`
```
sint(n) -> uint(p - q)
uint(n) -> uint(p - q)
```

## Infix

### `a eq b`, `a neq b`
```
Int sint -> bool
Int uint -> bool
sint Int -> bool
uint Int -> bool
```

### `a add b`, `a sub b`
```
Int sint(n)     -> sint(n + 1)
Int uint(n)     -> uint(n + 1)
sint(n) Int     -> sint(n + 1)
sint(n) sint(m) -> sint(max(n, m) + 1)
sint(n) uint(m) -> uint(max(n, m) + 1)
uint(n) Int     -> uint(n + 1)
uint(n) sint(m) -> uint(max(n, m) + 1)
uint(n) uint(m) -> uint(max(n, m) + 1)
```

### `a mul b`
```
Int sint(n)     -> sint(2 * n)
Int uint(n)     -> uint(2 * n)
sint(n) Int     -> sint(2 * n)
sint(n) sint(m) -> sint(n + m)
sint(n) uint(m) -> uint(n + m)
uint(n) Int     -> uint(2 * n)
uint(n) sint(m) -> uint(n + m)
uint(n) uint(m) -> uint(n + m)
```

### `a sl b`, `a sr b`, `a rotl b`, `a rotr b`
```
sint(n) Int     -> sint(n)
sint(n) uint(m) -> sint(n)
uint(n) Int     -> uint(n)
uint(n) uint(m) -> uint(n)
```

### `a sl_ext b`
```
sint(n) Int -> sint(n + b)
uint(n) Int -> uint(n + b)
```

### `a sr_tru b`
```
sint(n) Int -> sint(n - b)
uint(n) Int -> uint(n - b)
```

### `a and b`, `a or b`, `a xor b`, `a nand b`, `a nor b`, `a xnor b`
```
Int sint(n)     -> sint(n)
Int uint(n)     -> uint(n)
bool bool       -> bool
sint(n) Int     -> sint(n)
sint(n) sint(n) -> sint(n)
uint(n) Int     -> uint(n)
uint(n) uint(n) -> uint(n)
```

### `a cat b`
```
bool sint(n)    -> uint(n + 1)
bool uint(n)    -> uint(n + 1)
sint(n) bool    -> uint(n + 1)
sint(n) sint(m) -> uint(n + m)
sint(n) uint(m) -> uint(n + m)
uint(n) bool    -> uint(n + 1)
uint(n) sint(m) -> uint(n + m)
uint(n) uint(m) -> uint(n + m)
```

## Function

### `rep(n, x)`
```
Int _bool    -> uint(n)
Int _sint(m) -> uint(n * m)
Int _uint(m) -> uint(n * m)
```

### `inv(a)`
```
sint(n) -> sint(n)
uint(n) -> uint(n)
```

### `red_and(a)`, `red_or(a)`, `red_xor(a)`, `red_nand(a)`, `red_nor(a)`, `red_xnor(a)`
```
sint(n) -> bool
uint(n) -> bool
```

### `max(a, b)`, `min(a, b)`
```
Int sint(n)     -> sint(n)
Int uint(n)     -> uint(n)
sint(n) sint(n) -> sint(n)
uint(n) uint(n) -> uint(n)
```

### `signed(a)`
```
uint(n) -> sint(n)
```

### `unsigned(a)`
```
sint(n) -> uint(n)
```

## Other

### `len(a)`
```
data(n) -> Int
```

### `ext(n, a)`, `tru(n, a)`
```
Int sint(m) -> sint(n)
Int uint(m) -> uint(n)
```
