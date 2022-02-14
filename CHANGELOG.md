# Changelog

## [Unreleased]
### Importer
- Fix function override bugs.
- Merge expressions with descriptors.
- Add classes `Queue`, `AssociativeArray`, and `DynamicArray` to mirror SystemVerilog.
- Static functions as functions of companion objects.

### Compiler
- Rename `onr` to `oni`.
- Support secondary constructors.
- Replace `ENullExpression` with `ENothingExpression`.
- Support optional value parameters.
- Unwrap companion objects.
- Separately specialize nested classes.
- Support type parameterized imported classes.
- Remove support for constructor overloading.
- Replace `SimTop` and `SynthTop` with `EntryPoint`.
- Support initializer blocks.

## [0.1.13]
### Importer
- Preprocess include compiler directive.
- Preprocess file and line compiler directive.
- More rules for lexer and parser.
- Separate SystemVerilog and Kotlin AST.
- Descriptor AST element to track types.
- Document with original signature.
- Import tasks and functions.
- Import enum and struct typedefs.
- Import extern tasks and functions.
- Eliminate local type aliases.
- Support type parameters.

### Compiler
- Replace slice function with array indexing notation.
- Consolidate expression evaluation in stages specialize and evaluate.
- Extended immutability checking for array access expressions.
- Fix Kotlin compiler error message bug on Windows.
- Allow immutable input ports.

## [0.1.12]
### Compiler
- Support conditional compiler directives in importer.
- Support macro definitions in importer.
- Support decimal literals in `u` and `s` functions.
- Support tasks with return values.
- `fork` and `join` functions.
- Support x and z bit constants.
- Support type alias with type parameters.
- Evaluate logical operators and if expressions.
- Added end-to-end regression tests.
- Four state boolean values `unknown` and `floating`.
- Copy KDoc comments into generated SystemVerilog.
- Adopt `StageType` based stage sequence in importer.
- Turn off `enableLineDirective` by default.
- Rename `Logical` to `Optional`.
- Rewrite constant expression handling.
- Merge `Optional` with `Cardinal`.
- Separate `MID_TRANSFORM` into `UPPER_TRANSFORM` and `LOWER_TRANSFORM`.
- Rewrite specialize and resolve stages to move type resolution after specialization.
- New scheme for extracting expressions with `BlockExpressionReducerStage`.
- Merge `KtCallExpression` and `SvCallExpression`.
- Merge `KtBlockExpression` and `SvBlockExpression`.
- Merge `KtProperty` and `SvProperty`.
- Shorten function names on `Ubit` such as `sli`, `rev`, and `inv`.

## [0.1.11]
### Compiler
- `Verik` annotation to enable inspections by IntelliJ plugin.
- File annotation checker issues warnings if `Verik` annotation is missing.
- Consolidate interpreter checks to midCheck.
- Configuration validity checks.
- Specify entry points in `VerikPluginExtension`.

## [0.1.10]
### Plugin
- Upgrade junit to 5.8.2.
- Upgrade kotlin to 1.5.31.
- Upgrade jdk to 17.0.1.
 

### Compiler
- Multiline injected statements and injected properties.
- Log error and warning messages.
- Use SystemVerilog `` `line `` directive.
- Track declaration end locations.
- Reorder packages, files, and declarations based on dependencies.

## [0.1.9]
### Plugin
- Upgrade dokka to 1.5.30.

### Compiler
- Support inline combinational assignments.
- Track mutability of properties and primary constructor value parameters.
- Check port instantiations.
- Support as and is expressions.
- Introduce core declarations test framework.
- Support use of root package.
- Separate class interpreters.
- Reorganize core declarations.
- Move example projects to separate repository.

### Importer
- Introduce ANTLR based importer.
- Support SystemVerilog lexing and parsing.

## [0.1.8]
### Compiler
- Support virtual classes.
- Separate target declarations from core declarations.
- Introduce composite target declarations.
- Insert scope expressions.
- Support for `ArrayList`.
- Introduce synthesis and simulation top.
- Fix type equals type constraint bug.
- Organize outputs into `OutputContext`.
- Signature based core declaration map.

## [0.1.7]
### Compiler
- Support for width casting.
- Support for signed extension.
- Check widths during extension and truncation.
- Desugar class primary constructors.
- Support for initializer chaining.

## [0.1.6]
### Compiler
- Support for module interfaces.
- Support for clocking blocks.
- Support for module ports.
- Dangling reference checker.
- Dead code elimination.
- Unlift when expressions.
- Unlift if expressions.
- Platform dependent behavior for file paths.

## [0.1.5]
### Plugin
- Upgrade junit to 5.8.1.

### Compiler
- Support for array access expressions.
- Support for bit shifts.
- Support for packed and unpacked types.
- Reduction of for statements.
- Support for boolean logical functions.
- Labeling functions as automatic.

## [0.1.4]
### Compiler
- Parenthesis insertion by order of operations.
- Support for when expressions.
- Support for struct literals.
- Introduce classifier as generalization of type.
- Unwrap nested type aliases.
- Support for concatenation.
- Wrap properties with property statements.

## [0.1.3]
### Compiler
- Introduce type constraint framework.
- Type constraint solver and checker.
- Fizzbuzz example project.
- Support for module instantiations.
- Merge resolve and specialize stages.

## [0.1.2]
### Compiler
- Source serialization alignment.
- Stage based framework.
- Documentation for declarations borrowed from the Kotlin standard library.
- Support for named value arguments.
- Linting with ktlint.
- Support for suppressed and promoted warnings.
- General handling of annotations.

## [0.1.1]
### Plugin
- Separate plugin from compiler.
- GitHub actions for automatic releases.
- Clean Gradle build scripts.

## [0.1.0]
### Plugin
- Initial compiler Gradle plugin.
- Publishing through the Nexus Repository Manager.