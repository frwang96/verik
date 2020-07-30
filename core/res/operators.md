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

### `a % b`
```
Int sint(n) -> sint(n)
Int uint(n) -> uint(n)
sint(n) Int -> sint(n)
sint(n) sint(m) -> sint(min(n, m))
sint(n) uint(m) -> uint(min(n, m))
uint(n) Int -> uint(n)
uint(n) sint(m) -> uint(min(n, m))
uint(n) uint(m) -> uint(min(n, m))
```

### `a / b`
```
Int sint(n) -> sint(n)
Int uint(n) -> uint(n)
sint(n) Int -> sint(n)
sint(n) sint(m) -> sint(n)
sint(n) uint(m) -> uint(n)
uint(n) Int -> uint(n)
uint(n) sint(m) -> uint(n)
uint(n) uint(m) -> uint(n)
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

### `a sl b`, `a sr b`
```
sint(n) Int     -> sint(n)
sint(n) uint(m) -> sint(n)
uint(n) Int     -> uint(n)
uint(n) uint(m) -> uint(n)
```

### `a rotl b`, `a rotr b`
```
sint(n) Int     -> uint(n)
sint(n) uint(m) -> uint(n)
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
Int sint(n)     -> uint(n)
Int uint(n)     -> uint(n)
bool bool       -> bool
sint(n) Int     -> uint(n)
sint(n) sint(n) -> uint(n)
sint(n) uint(n) -> uint(n)
uint(n) Int     -> uint(n)
uint(n) sint(n) -> uint(n)
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

## assignment

### `a set_add b`, `a put_add b `, `a set_sub b`, `a put_sub b`, `a set_mul b`, `a put_mul b`
```
sint(n) Int
sint(n) sint(n)
uint(n) Int
uint(n) sint(n)
uint(n) uint(n)
```

### `a set_sl b`, `a put_sl b`, `a set_sr b`, `a put_sr b`
```
sint(n) Int
sint(n) uint(n)
uint(n) Int
uint(n) uint(n)
```

### `a set_rotl b`, `a put_rotl b`, `a set_rotr b`, `a put_rotr b`
```
uint(n) Int
uint(n) uint(n)
```

### `a set_and b`, `a put_and b`, `a set_or b`, `a put_or b`, `a set_xor b`, `a put_xor b`, `a set_nand b`, `a put_nand b`, `a set_nor b`, `a put_nor b`, `a set_xnor b`, `a put_xnor b`
```
bool bool
uint(n) Int
uint(n) sint(n)
uint(n) uint(n)
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
sint(n) -> uint(n)
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
