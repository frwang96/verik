# Changelog

## [Unreleased]
### Added
- Support conditional compiler directives in importer.

### Changed
- Adopt `StageType` based stage sequence in importer.

## [0.1.11]
### Added
- `Verik` annotation to enable inspections by IntelliJ plugin.
- File annotation checker issues warnings if `Verik` annotation is missing.
- Consolidate interpreter checks to midCheck.
- Configuration validity checks.

### Changed
- Specify entry points in `VerikPluginExtension`.

## [0.1.10]
### Added
- Multiline injected statements and injected properties.
- Log error and warning messages.
- Use SystemVerilog `` `line `` directive.
- Track declaration end locations.
- Reorder packages, files, and declarations based on dependencies.

### Changed
- Upgrade junit to 5.8.2.
- Upgrade kotlin to 1.5.31.
- Upgrade jdk to 17.0.1.

## [0.1.9]
### Added
- Support inline combinational assignments.
- Track mutability of properties and primary constructor value parameters.
- Check port instantiations.
- Support as and is expressions.
- Introduce core declarations test framework.
- Introduce ANTLR based importer.
- Support SystemVerilog lexing and parsing.
- Support use of root package.

### Changed
- Upgrade dokka to 1.5.30.
- Separate class interpreters.
- Reorganize core declarations.
- Move example projects to separate repository.

## [0.1.8]
### Added
- Support virtual classes.
- Separate target declarations from core declarations.
- Introduce composite target declarations.
- Insert scope expressions.
- Support for `ArrayList`.
- Introduce synthesis and simulation top.

### Changed
- Fix type equals type constraint bug.
- Organize outputs into `OutputContext`.
- Signature based core declaration map.

## [0.1.7]
### Added
- Support for width casting.
- Support for signed extension.
- Check widths during extension and truncation.
- Desugar class primary constructors.
- Support for initializer chaining.

## [0.1.6]
### Added
- Support for module interfaces.
- Support for clocking blocks.
- Support for module ports.
- Dangling reference checker.
- Dead code elimination.
- Unlift when expressions.
- Unlift if expressions.
- Platform dependent behavior for file paths.

## [0.1.5]
### Added
- Support for array access expressions.
- Support for bit shifts.
- Support for packed and unpacked types.
- Reduction of for statements.
- Support for boolean logical functions.
- Labeling functions as automatic.

### Changed
- Upgrade junit to 5.8.1.

## [0.1.4]
### Added
- Parenthesis insertion by order of operations.
- Support for when expressions.
- Support for struct literals.
- Introduce classifier as generalization of type.
- Unwrap nested type aliases.
- Support for concatenation.

### Changed
- Wrap properties with property statements.

## [0.1.3]
### Added
- Introduce type constraint framework.
- Type constraint solver and checker.
- Fizzbuzz example project.
- Support for module instantiations.

### Changed
- Merge resolve and specialize stages.

## [0.1.2]
### Added
- Source serialization alignment.
- Stage based compiler framework.
- Documentation for declarations borrowed from the Kotlin standard library.
- Support for named value arguments.
- Linting with ktlint.
- Support for suppressed and promoted warnings.

### Changed
- General handling of annotations.

## [0.1.1]
### Added
- Separate plugin from compiler.
- GitHub actions for automatic releases.

### Changed
- Clean Gradle build scripts.

## [0.1.0]
### Added
- Initial compiler Gradle plugin.
- Publishing through the Nexus Repository Manager.