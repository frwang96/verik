// Generated from /Users/francis/Documents/Work/Verik/git.nosync/verik/verik-importer/src/main/gen/io/verik/importer/antlr/SystemVerilogParser.g4 by ANTLR 4.9.2
package io.verik.importer.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SystemVerilogParser}.
 */
public interface SystemVerilogParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void enterCompilationUnit(SystemVerilogParser.CompilationUnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void exitCompilationUnit(SystemVerilogParser.CompilationUnitContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#description}.
	 * @param ctx the parse tree
	 */
	void enterDescription(SystemVerilogParser.DescriptionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#description}.
	 * @param ctx the parse tree
	 */
	void exitDescription(SystemVerilogParser.DescriptionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#moduleNonAnsiHeader}.
	 * @param ctx the parse tree
	 */
	void enterModuleNonAnsiHeader(SystemVerilogParser.ModuleNonAnsiHeaderContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#moduleNonAnsiHeader}.
	 * @param ctx the parse tree
	 */
	void exitModuleNonAnsiHeader(SystemVerilogParser.ModuleNonAnsiHeaderContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#moduleAnsiHeader}.
	 * @param ctx the parse tree
	 */
	void enterModuleAnsiHeader(SystemVerilogParser.ModuleAnsiHeaderContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#moduleAnsiHeader}.
	 * @param ctx the parse tree
	 */
	void exitModuleAnsiHeader(SystemVerilogParser.ModuleAnsiHeaderContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#moduleDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterModuleDeclaration(SystemVerilogParser.ModuleDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#moduleDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitModuleDeclaration(SystemVerilogParser.ModuleDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#listOfPorts}.
	 * @param ctx the parse tree
	 */
	void enterListOfPorts(SystemVerilogParser.ListOfPortsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#listOfPorts}.
	 * @param ctx the parse tree
	 */
	void exitListOfPorts(SystemVerilogParser.ListOfPortsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#listOfPortDeclarations}.
	 * @param ctx the parse tree
	 */
	void enterListOfPortDeclarations(SystemVerilogParser.ListOfPortDeclarationsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#listOfPortDeclarations}.
	 * @param ctx the parse tree
	 */
	void exitListOfPortDeclarations(SystemVerilogParser.ListOfPortDeclarationsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#portDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterPortDeclaration(SystemVerilogParser.PortDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#portDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitPortDeclaration(SystemVerilogParser.PortDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#port}.
	 * @param ctx the parse tree
	 */
	void enterPort(SystemVerilogParser.PortContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#port}.
	 * @param ctx the parse tree
	 */
	void exitPort(SystemVerilogParser.PortContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#portExpression}.
	 * @param ctx the parse tree
	 */
	void enterPortExpression(SystemVerilogParser.PortExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#portExpression}.
	 * @param ctx the parse tree
	 */
	void exitPortExpression(SystemVerilogParser.PortExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#portReference}.
	 * @param ctx the parse tree
	 */
	void enterPortReference(SystemVerilogParser.PortReferenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#portReference}.
	 * @param ctx the parse tree
	 */
	void exitPortReference(SystemVerilogParser.PortReferenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#portDirection}.
	 * @param ctx the parse tree
	 */
	void enterPortDirection(SystemVerilogParser.PortDirectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#portDirection}.
	 * @param ctx the parse tree
	 */
	void exitPortDirection(SystemVerilogParser.PortDirectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#netPortHeader}.
	 * @param ctx the parse tree
	 */
	void enterNetPortHeader(SystemVerilogParser.NetPortHeaderContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#netPortHeader}.
	 * @param ctx the parse tree
	 */
	void exitNetPortHeader(SystemVerilogParser.NetPortHeaderContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#ansiPortDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterAnsiPortDeclaration(SystemVerilogParser.AnsiPortDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#ansiPortDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitAnsiPortDeclaration(SystemVerilogParser.AnsiPortDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#moduleItem}.
	 * @param ctx the parse tree
	 */
	void enterModuleItem(SystemVerilogParser.ModuleItemContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#moduleItem}.
	 * @param ctx the parse tree
	 */
	void exitModuleItem(SystemVerilogParser.ModuleItemContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#packageItem}.
	 * @param ctx the parse tree
	 */
	void enterPackageItem(SystemVerilogParser.PackageItemContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#packageItem}.
	 * @param ctx the parse tree
	 */
	void exitPackageItem(SystemVerilogParser.PackageItemContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#packageOrGenerateItemDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterPackageOrGenerateItemDeclaration(SystemVerilogParser.PackageOrGenerateItemDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#packageOrGenerateItemDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitPackageOrGenerateItemDeclaration(SystemVerilogParser.PackageOrGenerateItemDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#inputDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInputDeclaration(SystemVerilogParser.InputDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#inputDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInputDeclaration(SystemVerilogParser.InputDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#outputDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterOutputDeclaration(SystemVerilogParser.OutputDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#outputDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitOutputDeclaration(SystemVerilogParser.OutputDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#dataDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterDataDeclaration(SystemVerilogParser.DataDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#dataDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitDataDeclaration(SystemVerilogParser.DataDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#dataType}.
	 * @param ctx the parse tree
	 */
	void enterDataType(SystemVerilogParser.DataTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#dataType}.
	 * @param ctx the parse tree
	 */
	void exitDataType(SystemVerilogParser.DataTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#dataTypeOrImplicit}.
	 * @param ctx the parse tree
	 */
	void enterDataTypeOrImplicit(SystemVerilogParser.DataTypeOrImplicitContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#dataTypeOrImplicit}.
	 * @param ctx the parse tree
	 */
	void exitDataTypeOrImplicit(SystemVerilogParser.DataTypeOrImplicitContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#implicitDataType}.
	 * @param ctx the parse tree
	 */
	void enterImplicitDataType(SystemVerilogParser.ImplicitDataTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#implicitDataType}.
	 * @param ctx the parse tree
	 */
	void exitImplicitDataType(SystemVerilogParser.ImplicitDataTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#integerType}.
	 * @param ctx the parse tree
	 */
	void enterIntegerType(SystemVerilogParser.IntegerTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#integerType}.
	 * @param ctx the parse tree
	 */
	void exitIntegerType(SystemVerilogParser.IntegerTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#integerVectorType}.
	 * @param ctx the parse tree
	 */
	void enterIntegerVectorType(SystemVerilogParser.IntegerVectorTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#integerVectorType}.
	 * @param ctx the parse tree
	 */
	void exitIntegerVectorType(SystemVerilogParser.IntegerVectorTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#netType}.
	 * @param ctx the parse tree
	 */
	void enterNetType(SystemVerilogParser.NetTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#netType}.
	 * @param ctx the parse tree
	 */
	void exitNetType(SystemVerilogParser.NetTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#netPortType}.
	 * @param ctx the parse tree
	 */
	void enterNetPortType(SystemVerilogParser.NetPortTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#netPortType}.
	 * @param ctx the parse tree
	 */
	void exitNetPortType(SystemVerilogParser.NetPortTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#variablePortType}.
	 * @param ctx the parse tree
	 */
	void enterVariablePortType(SystemVerilogParser.VariablePortTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#variablePortType}.
	 * @param ctx the parse tree
	 */
	void exitVariablePortType(SystemVerilogParser.VariablePortTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#varDataType}.
	 * @param ctx the parse tree
	 */
	void enterVarDataType(SystemVerilogParser.VarDataTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#varDataType}.
	 * @param ctx the parse tree
	 */
	void exitVarDataType(SystemVerilogParser.VarDataTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#signing}.
	 * @param ctx the parse tree
	 */
	void enterSigning(SystemVerilogParser.SigningContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#signing}.
	 * @param ctx the parse tree
	 */
	void exitSigning(SystemVerilogParser.SigningContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#simpleType}.
	 * @param ctx the parse tree
	 */
	void enterSimpleType(SystemVerilogParser.SimpleTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#simpleType}.
	 * @param ctx the parse tree
	 */
	void exitSimpleType(SystemVerilogParser.SimpleTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#listOfVariableDeclAssignments}.
	 * @param ctx the parse tree
	 */
	void enterListOfVariableDeclAssignments(SystemVerilogParser.ListOfVariableDeclAssignmentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#listOfVariableDeclAssignments}.
	 * @param ctx the parse tree
	 */
	void exitListOfVariableDeclAssignments(SystemVerilogParser.ListOfVariableDeclAssignmentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#variableDeclAssignment}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclAssignment(SystemVerilogParser.VariableDeclAssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#variableDeclAssignment}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclAssignment(SystemVerilogParser.VariableDeclAssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#packedDimension}.
	 * @param ctx the parse tree
	 */
	void enterPackedDimension(SystemVerilogParser.PackedDimensionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#packedDimension}.
	 * @param ctx the parse tree
	 */
	void exitPackedDimension(SystemVerilogParser.PackedDimensionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#constantExpression}.
	 * @param ctx the parse tree
	 */
	void enterConstantExpression(SystemVerilogParser.ConstantExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#constantExpression}.
	 * @param ctx the parse tree
	 */
	void exitConstantExpression(SystemVerilogParser.ConstantExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#constantRange}.
	 * @param ctx the parse tree
	 */
	void enterConstantRange(SystemVerilogParser.ConstantRangeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#constantRange}.
	 * @param ctx the parse tree
	 */
	void exitConstantRange(SystemVerilogParser.ConstantRangeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#constantPrimary}.
	 * @param ctx the parse tree
	 */
	void enterConstantPrimary(SystemVerilogParser.ConstantPrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#constantPrimary}.
	 * @param ctx the parse tree
	 */
	void exitConstantPrimary(SystemVerilogParser.ConstantPrimaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#primaryLiteral}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryLiteral(SystemVerilogParser.PrimaryLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#primaryLiteral}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryLiteral(SystemVerilogParser.PrimaryLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(SystemVerilogParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(SystemVerilogParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#integralNumber}.
	 * @param ctx the parse tree
	 */
	void enterIntegralNumber(SystemVerilogParser.IntegralNumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#integralNumber}.
	 * @param ctx the parse tree
	 */
	void exitIntegralNumber(SystemVerilogParser.IntegralNumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#decimalNumber}.
	 * @param ctx the parse tree
	 */
	void enterDecimalNumber(SystemVerilogParser.DecimalNumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#decimalNumber}.
	 * @param ctx the parse tree
	 */
	void exitDecimalNumber(SystemVerilogParser.DecimalNumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogParser#identifier}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(SystemVerilogParser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogParser#identifier}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(SystemVerilogParser.IdentifierContext ctx);
}