// Generated from /Users/francis/Documents/Work/Verik/git.nosync/verik/verik-importer/src/main/gen/io/verik/importer/antlr/SystemVerilogParser.g4 by ANTLR 4.9.2
package io.verik.importer.antlr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SystemVerilogParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SystemVerilogParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#compilationUnit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompilationUnit(SystemVerilogParser.CompilationUnitContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#description}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDescription(SystemVerilogParser.DescriptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#moduleNonAnsiHeader}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModuleNonAnsiHeader(SystemVerilogParser.ModuleNonAnsiHeaderContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#moduleAnsiHeader}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModuleAnsiHeader(SystemVerilogParser.ModuleAnsiHeaderContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#moduleDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModuleDeclaration(SystemVerilogParser.ModuleDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#listOfPorts}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListOfPorts(SystemVerilogParser.ListOfPortsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#listOfPortDeclarations}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListOfPortDeclarations(SystemVerilogParser.ListOfPortDeclarationsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#portDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPortDeclaration(SystemVerilogParser.PortDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#port}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPort(SystemVerilogParser.PortContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#portExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPortExpression(SystemVerilogParser.PortExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#portReference}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPortReference(SystemVerilogParser.PortReferenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#portDirection}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPortDirection(SystemVerilogParser.PortDirectionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#netPortHeader}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNetPortHeader(SystemVerilogParser.NetPortHeaderContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#ansiPortDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnsiPortDeclaration(SystemVerilogParser.AnsiPortDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#moduleItem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModuleItem(SystemVerilogParser.ModuleItemContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#packageItem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPackageItem(SystemVerilogParser.PackageItemContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#packageOrGenerateItemDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPackageOrGenerateItemDeclaration(SystemVerilogParser.PackageOrGenerateItemDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#inputDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInputDeclaration(SystemVerilogParser.InputDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#outputDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutputDeclaration(SystemVerilogParser.OutputDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#dataDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDataDeclaration(SystemVerilogParser.DataDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#dataType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDataType(SystemVerilogParser.DataTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#dataTypeOrImplicit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDataTypeOrImplicit(SystemVerilogParser.DataTypeOrImplicitContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#implicitDataType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImplicitDataType(SystemVerilogParser.ImplicitDataTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#integerType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntegerType(SystemVerilogParser.IntegerTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#integerVectorType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntegerVectorType(SystemVerilogParser.IntegerVectorTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#netType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNetType(SystemVerilogParser.NetTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#netPortType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNetPortType(SystemVerilogParser.NetPortTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#variablePortType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariablePortType(SystemVerilogParser.VariablePortTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#varDataType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDataType(SystemVerilogParser.VarDataTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#signing}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSigning(SystemVerilogParser.SigningContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#simpleType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleType(SystemVerilogParser.SimpleTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#listOfVariableDeclAssignments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListOfVariableDeclAssignments(SystemVerilogParser.ListOfVariableDeclAssignmentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#variableDeclAssignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclAssignment(SystemVerilogParser.VariableDeclAssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#packedDimension}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPackedDimension(SystemVerilogParser.PackedDimensionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#constantExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstantExpression(SystemVerilogParser.ConstantExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#constantRange}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstantRange(SystemVerilogParser.ConstantRangeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#constantPrimary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstantPrimary(SystemVerilogParser.ConstantPrimaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#primaryLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimaryLiteral(SystemVerilogParser.PrimaryLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(SystemVerilogParser.NumberContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#integralNumber}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntegralNumber(SystemVerilogParser.IntegralNumberContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#decimalNumber}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecimalNumber(SystemVerilogParser.DecimalNumberContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogParser#identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(SystemVerilogParser.IdentifierContext ctx);
}