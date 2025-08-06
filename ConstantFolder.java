package parser;

/*
   Performs constant folding optimization on an abstract syntax tree (AST).
   Constant folding is a compiler optimization that evaluates constant expressions at compile time.
 */
public class ConstantFolder {

    /*
       Recursively folds constant expressions in the AST.
       x = 3 * 5;
       return a simplified AST node x = 15 .
     */
    public ASTNode fold(ASTNode node) {

        // If the node is a binary operation, check if its operands are constants
        if (node instanceof ASTNode.BinOpNode binOp) {
            ASTNode left = fold(binOp.left);   // Recursively fold left operand
            ASTNode right = fold(binOp.right); // Recursively fold right operand

            // If both operands are integers, evaluate the result at compile time
            if (left instanceof ASTNode.IntNode lInt && right instanceof ASTNode.IntNode rInt) {
                switch (binOp.op) {
                    case "+": return new ASTNode.IntNode(lInt.value + rInt.value);
                    case "-": return new ASTNode.IntNode(lInt.value - rInt.value);
                    case "*": return new ASTNode.IntNode(lInt.value * rInt.value);
                    case "/": return new ASTNode.IntNode(lInt.value / rInt.value);
                    default:  return new ASTNode.BinOpNode(left, binOp.op, right); // Unrecognized operator
                }
            }

            // If both operands are floats, evaluate the result at compile time
            if (left instanceof ASTNode.FloatNode lFloat && right instanceof ASTNode.FloatNode rFloat) {
                switch (binOp.op) {
                    case "+": return new ASTNode.FloatNode(lFloat.value + rFloat.value);
                    case "-": return new ASTNode.FloatNode(lFloat.value - rFloat.value);
                    case "*": return new ASTNode.FloatNode(lFloat.value * rFloat.value);
                    case "/": return new ASTNode.FloatNode(lFloat.value / rFloat.value);
                    default:  return new ASTNode.BinOpNode(left, binOp.op, right); // Unrecognized operator
                }
            }

            // If the operands are mixed (int and float), convert int to float and retry folding
            if (left instanceof ASTNode.IntNode li && right instanceof ASTNode.FloatNode rf) {
                return fold(new ASTNode.BinOpNode(new ASTNode.FloatNode(li.value), binOp.op, rf));
            }
            if (left instanceof ASTNode.FloatNode lf && right instanceof ASTNode.IntNode ri) {
                return fold(new ASTNode.BinOpNode(lf, binOp.op, new ASTNode.FloatNode(ri.value)));
            }

            // If operands are not both constants, return updated BinOpNode
            return new ASTNode.BinOpNode(left, binOp.op, right);
        }

        // Handle assignment folding (but don't fold the RHS here, just return a copy)
        else if (node instanceof ASTNode.AssignNode assign) {
            return new ASTNode.AssignNode(assign.varName, assign.value, assign.declaredType);
        }

        // Fold expressions inside if statements
        else if (node instanceof ASTNode.IfNode ifNode) {
            ASTNode cond = fold(ifNode.condition);
            ASTNode body = fold(ifNode.thenBlock);
            ASTNode els = ifNode.elseBlock != null ? fold(ifNode.elseBlock) : null;
            return new ASTNode.IfNode(cond, body, els);
        }

        // Fold components of a for loop (though currently does not fold init/update/cond deeply)
        else if (node instanceof ASTNode.ForNode forNode) {
            ASTNode body = fold(forNode.body);
            return new ASTNode.ForNode(
                    forNode.init, forNode.condition, forNode.update, body
            );
        }

        // Fold expressions inside while loops
        else if (node instanceof ASTNode.WhileNode whileNode) {
            ASTNode body = fold(whileNode.body);
            return new ASTNode.WhileNode(
                    whileNode.condition, body
            );
        }

        // Fold return expressions
        else if (node instanceof ASTNode.ReturnNode returnNode) {
            return new ASTNode.ReturnNode(returnNode.value != null ? fold(returnNode.value) : null);
        }

        return node;
    }
}
