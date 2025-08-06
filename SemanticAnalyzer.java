package parser;

import java.util.HashMap;
import java.util.Map;


//   Performs semantic analysis on the abstract syntax tree (AST).
//   This includes type checking, variable declaration checking, and ensuring type compatibility in expressions.

public class SemanticAnalyzer {

    // A symbol table to keep track of declared variables and their types
    private final Map<String, String> symbolTable = new HashMap<>();

    /*
       Entry point to analyze a given AST node.
       Call to specific methods depending on that node type.
     */
    public void analyze(ASTNode node) {
        if (node instanceof ASTNode.AssignNode) {
            analyzeAssignNode((ASTNode.AssignNode) node);
        } else if (node instanceof ASTNode.BinOpNode) {
            analyzeBinOpNode((ASTNode.BinOpNode) node);
        } else if (node instanceof ASTNode.IfNode) {
            analyzeIfNode((ASTNode.IfNode) node);
        } else if (node instanceof ASTNode.ForNode) {
            analyzeForNode((ASTNode.ForNode) node);
        } else if (node instanceof ASTNode.WhileNode) {
            analyzeWhileNode((ASTNode.WhileNode) node);
        } else if (node instanceof ASTNode.ReturnNode) {
            checkReturnNode((ASTNode.ReturnNode) node);
        }
    }


    // Handles type checking and variable declaration in assignment statements.
    private void analyzeAssignNode(ASTNode.AssignNode assignNode) {
        // Determine the type of the right-hand side expression
        String rhsType = getExpressionType(assignNode.value);

        // Declare the variable if it has a declared type
        if (assignNode.declaredType != null) {
            declareVariable(assignNode.varName, assignNode.declaredType);
        }

        // Get the variable's type from the symbol table
        String lhsType = symbolTable.get(assignNode.varName);
        if (lhsType == null) {
            throw new RuntimeException("Undeclared variable: " + assignNode.varName);
        }

        // Type compatibility checks
        if ("string".equals(lhsType) && !"string".equals(rhsType)) {
            throw new RuntimeException("Cannot assign non-string to string.");
        }
        if ("int".equals(lhsType) && "float".equals(rhsType)) {
            throw new RuntimeException("Semantic error: Type mismatch. Cannot assign float to int.");
        }
        if ("float".equals(lhsType) && "int".equals(rhsType)) {
            System.out.println("Implicit casting: Assigning int to float.");
        }
        if (!lhsType.equals(rhsType) &&
                !(lhsType.equals("float") && rhsType.equals("int"))) {
            throw new RuntimeException("Semantic error: Type mismatch. Cannot assign " + rhsType + " to " + lhsType);
        }
    }

    /*
       Adds a new variable to the symbol table.
       Throws an error if the variable is already declared.
     */
    private void declareVariable(String name, String type) {
        if (symbolTable.containsKey(name)) {
            throw new RuntimeException("Variable already declared: " + name);
        }
        symbolTable.put(name, type);
    }


    // Performs type checking on binary operations.
    private void analyzeBinOpNode(ASTNode.BinOpNode binOpNode) {
        String leftType = getExpressionType(binOpNode.left);
        String rightType = getExpressionType(binOpNode.right);

        // Allow mixed int/float arithmetic, but disallow incompatible types
        if (!leftType.equals(rightType) &&
                !(leftType.equals("float") && rightType.equals("int")) &&
                !(leftType.equals("int") && rightType.equals("float"))) {
            throw new RuntimeException("Semantic error: Type mismatch in binary operation: " +
                    leftType + " " + binOpNode.op + " " + rightType);
        }
    }

    // Checks that the condition in an if-statement is a valid numeric type (int or float).
    private void analyzeIfNode(ASTNode.IfNode ifNode) {
        String condType = getExpressionType(ifNode.condition);
        if (!"int".equals(condType) && !"float".equals(condType)) {
            throw new RuntimeException("Semantic error: Invalid condition type in if statement. Expected int or float, got " + condType);
        }
    }

    /*
       Performs semantic analysis of a for-loop:
        Initializes the loop
        Checks the loop condition and update
        Analyzes the loop body
     */
    private void analyzeForNode(ASTNode.ForNode forNode) {
        analyze(forNode.init);   // Initialization
        String condType = getExpressionType(forNode.condition);
        if (!"int".equals(condType) && !"float".equals(condType)) {
            throw new RuntimeException("Semantic error: Invalid condition type in for loop. Expected int or float, got " + condType);
        }
        analyze(forNode.update); // Increment/decrement
        analyze(forNode.body);   // Loop body
    }

    // Checks the condition expression in a while loop
    private void analyzeWhileNode(ASTNode.WhileNode whileNode) {
        String condType = getExpressionType(whileNode.condition);
        if (!"int".equals(condType) && !"float".equals(condType)) {
            throw new RuntimeException("Semantic error: Invalid condition type in while statement. Expected int or float, got " + condType);
        }
        analyze(whileNode.body); // Loop body
    }


    // Checks the return type of  return statement
    private void checkReturnNode(ASTNode.ReturnNode node) {
        if (node.value != null) {
            String returnType = getExpressionType(node.value);
            if ("unknown".equals(returnType)) {
                throw new RuntimeException("Return statement returns unknown type.");
            }
        }
    }

    // Determines the type of  given expression node
    private String getExpressionType(ASTNode node) {
        if (node instanceof ASTNode.IntNode) {
            return "int";
        } else if (node instanceof ASTNode.FloatNode) {
            return "float";
        } else if (node instanceof ASTNode.StringNode) {
            return "string";
        } else if (node instanceof ASTNode.VarNode) {
            return getVariableType((ASTNode.VarNode) node);
        } else if (node instanceof ASTNode.BinOpNode) {
            return inferBinOpType((ASTNode.BinOpNode) node);
        } else {
            return "unknown";
        }
    }

    // Retrieves the type of  variable from the symbol table
    private String getVariableType(ASTNode.VarNode varNode) {
        String type = symbolTable.get(varNode.name);
        if (type == null) {
            throw new RuntimeException("Undeclared variable: " + varNode.name);
        }
        return type;
    }


    // Check the result type of binary operation based on operand types
    private String inferBinOpType(ASTNode.BinOpNode binOpNode) {
        String leftType = getExpressionType(binOpNode.left);
        String rightType = getExpressionType(binOpNode.right);

        if ("int".equals(leftType) && "int".equals(rightType)) {
            return "int";
        } else if ("float".equals(leftType) && "float".equals(rightType)) {
            return "float";
        } else if (("int".equals(leftType) && "float".equals(rightType)) ||
                ("float".equals(leftType) && "int".equals(rightType))) {
            return "float";
        } else {
            throw new RuntimeException("Semantic error: Incompatible types in binary operation.");
        }
    }
}
