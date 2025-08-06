package parser;

import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;


// Abstract base class for all AST nodes
public abstract class ASTNode {

    // Print method to be implemented by all concrete node types.
    public abstract void print(String indent);

    // Represents a variable assignment, with optional type declaration
    public static class AssignNode extends ASTNode {
        public final String varName;
        public final ASTNode value;
        public final String declaredType;

        public AssignNode(String varName, ASTNode value, String declaredType) {
            this.varName = varName;
            this.value = value;
            this.declaredType = declaredType;
        }

        @Override
        public String toString() {
            return "AssignNode(" + (declaredType != null ? declaredType + " " : "") + varName + " = " + value + ")";
        }

        @Override
        public void print(String indent) {
            System.out.println(indent + "AssignNode: " + (declaredType != null ? declaredType + " " : "") + varName);
            value.print(indent + "  ");
        }
    }

    // Represents a binary operation like +, -, *, /, etc
    public static class BinOpNode extends ASTNode {
        public final ASTNode left;
        public final String op;
        public final ASTNode right;

        public BinOpNode(ASTNode left, String op, ASTNode right) {
            this.left = left;
            this.op = op;
            this.right = right;
        }

        @Override
        public String toString() {
            return "BinOpNode(" + left + " " + op + " " + right + ")";
        }

        @Override
        public void print(String indent) {
            System.out.println(indent + "Binary Operation: " + op);
            left.print(indent + "  ");
            right.print(indent + "  ");
        }

    }


    // Represents an integer literal value
    public static class IntNode extends ASTNode {
        public final int value;

        public IntNode(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return Integer.toString(value);
        }

        @Override
        public void print(String indent) {
            System.out.println(indent + "Integer: " + value);
        }


    }


    // Represents a floating literal
    public static class FloatNode extends ASTNode {
        public final double value;

        public FloatNode(double value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return Double.toString(value);
        }

        @Override
        public void print(String indent) {
            System.out.println(indent + "Float: " + value);
        }

    }

    // Represents a string literal
    public static class StringNode extends ASTNode {
        public final String value;

        public StringNode(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "\"" + value + "\"";
        }

        @Override
        public void print(String indent) {
            System.out.println(indent + "String: \"" + value + "\"");
        }
    }


    // Represents an if-else conditional statement
    public static class IfNode extends ASTNode {
        public final ASTNode condition;
        public final ASTNode thenBlock;
        public final ASTNode elseBlock;

        public IfNode(ASTNode condition, ASTNode thenBlock, ASTNode elseBlock) {
            this.condition = condition;
            this.thenBlock = thenBlock;
            this.elseBlock = elseBlock;
        }

        @Override
        public String toString() {
            return "IfNode(cond=" + condition + ", then=" + thenBlock + ", else=" + elseBlock + ")";
        }

        @Override
        public void print(String indent) {
            System.out.println(indent + "If Statement:");
            condition.print(indent + "  ");
            System.out.println(indent + "  Then:");
            thenBlock.print(indent + "    ");
            if (elseBlock != null) {
                System.out.println(indent + "  Else:");
                elseBlock.print(indent + "    ");
            }
        }
    }

    // Represents a for-loop construct
    public static class ForNode extends ASTNode {
        public final ASTNode init;
        public final ASTNode condition;
        public final ASTNode update;
        public final ASTNode body;

        public ForNode(ASTNode init, ASTNode condition, ASTNode update, ASTNode body) {
            this.init = init;
            this.condition = condition;
            this.update = update;
            this.body = body;
        }

        @Override
        public String toString() {
            return "ForNode(init=" + init + ", cond=" + condition + ", update=" + update + ", body=" + body + ")";
        }

        @Override
        public void print(String indent) {
            System.out.println(indent + "For Loop:");
            System.out.println(indent + "  Initialization:");
            init.print(indent + "    ");
            System.out.println(indent + "  Condition:");
            condition.print(indent + "    ");
            System.out.println(indent + "  Update:");
            update.print(indent + "    ");
            System.out.println(indent + "  Body:");
            body.print(indent + "    ");
        }
    }

    // Represents a while-loop
    public static class WhileNode extends ASTNode {
        public final ASTNode condition;
        public final ASTNode body;

        public WhileNode(ASTNode condition, ASTNode body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        public String toString() {
            return "WhileNode(cond=" + condition + ", body=" + body + ")";
        }

        @Override
        public void print(String indent) {
            System.out.println(indent + "While Statement: ");
            condition.print(indent + "  ");
            body.print(indent + "  ");
        }
    }


    // Represents a return statement with a return value
    public static class ReturnNode extends ASTNode {
        public final ASTNode value;

        public ReturnNode(ASTNode value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "ReturnNode(value=" + value + ")";
        }

        @Override
        public void print(String indent) {
            System.out.println(indent + "Return:");
            if (value != null) {
                value.print(indent + "  ");
            } else {
                System.out.println(indent + "  (empty)");
            }
        }

    }


    // Represents a variable name
    public static class VarNode extends ASTNode {
        public String name;

        public VarNode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "VarNode(" + name + ")";
        }

        @Override
        public void print(String indent) {
            System.out.println(indent + "VarNode(" + name + ")");
        }

    }


    // Represents a block of statements (like a code block within braces)
    public static class BlockNode extends ASTNode {
        public List<ASTNode> statements;

        public BlockNode(List<ASTNode> statements) {
            this.statements = statements;
        }

        @Override
        public String toString() {
            return "BlockNode(" + statements + ")";
        }

        @Override
        public void print(String indent) {
            System.out.println(indent + "{");
            for (ASTNode stmt : statements) {
                stmt.print(indent + "  ");
            }
            System.out.println(indent + "}");
        }
    }


    // Builds a Swing-compatible tree structure for visualizing the AST
    public DefaultMutableTreeNode buildTreeNode(ASTNode node) {
        if (node == null) return new DefaultMutableTreeNode("(null)");

        // Each node type gets mapped to a tree node with children
        // corresponding to its components

        if (node instanceof ASTNode.AssignNode assign) {
            String label = "AssignNode: " + (assign.declaredType != null ? assign.declaredType + " " : "") + assign.varName;
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(label);
            root.add(buildTreeNode(assign.value));
            return root;
        }

        else if (node instanceof ASTNode.BinOpNode binOp) {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Binary Operation: " + binOp.op);
            root.add(buildTreeNode(binOp.left));
            root.add(buildTreeNode(binOp.right));
            return root;
        }

        else if (node instanceof ASTNode.IntNode intNode) {
            return new DefaultMutableTreeNode("Integer: " + intNode.value);
        }

        else if (node instanceof ASTNode.FloatNode floatNode) {
            return new DefaultMutableTreeNode("Float: " + floatNode.value);
        }

        else if (node instanceof ASTNode.StringNode strNode) {
            return new DefaultMutableTreeNode("String: \"" + strNode.value + "\"");
        }

        else if (node instanceof ASTNode.VarNode varNode) {
            return new DefaultMutableTreeNode("VarNode: " + varNode.name);
        }

        else if (node instanceof ASTNode.IfNode ifNode) {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("If Statement");

            DefaultMutableTreeNode conditionNode = new DefaultMutableTreeNode("Condition:");
            conditionNode.add(buildTreeNode(ifNode.condition));
            root.add(conditionNode);

            DefaultMutableTreeNode thenNode = new DefaultMutableTreeNode("Then:");
            thenNode.add(buildTreeNode(ifNode.thenBlock));
            root.add(thenNode);

            if (ifNode.elseBlock != null) {
                DefaultMutableTreeNode elseNode = new DefaultMutableTreeNode("Else:");
                elseNode.add(buildTreeNode(ifNode.elseBlock));
                root.add(elseNode);
            }

            return root;
        }

        else if (node instanceof ASTNode.ForNode forNode) {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("For Loop");

            DefaultMutableTreeNode initNode = new DefaultMutableTreeNode("Initialization:");
            initNode.add(buildTreeNode(forNode.init));
            root.add(initNode);

            DefaultMutableTreeNode conditionNode = new DefaultMutableTreeNode("Condition:");
            conditionNode.add(buildTreeNode(forNode.condition));
            root.add(conditionNode);

            DefaultMutableTreeNode updateNode = new DefaultMutableTreeNode("Update:");
            updateNode.add(buildTreeNode(forNode.update));
            root.add(updateNode);

            DefaultMutableTreeNode bodyNode = new DefaultMutableTreeNode("Body:");
            bodyNode.add(buildTreeNode(forNode.body));
            root.add(bodyNode);

            return root;
        }

        else if (node instanceof ASTNode.WhileNode whileNode) {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("While Loop");

            DefaultMutableTreeNode condNode = new DefaultMutableTreeNode("Condition:");
            condNode.add(buildTreeNode(whileNode.condition));
            root.add(condNode);

            DefaultMutableTreeNode bodyNode = new DefaultMutableTreeNode("Body:");
            bodyNode.add(buildTreeNode(whileNode.body));
            root.add(bodyNode);

            return root;
        }

        else if (node instanceof ASTNode.ReturnNode returnNode) {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Return");
            if (returnNode.value != null) {
                root.add(buildTreeNode(returnNode.value));
            } else {
                root.add(new DefaultMutableTreeNode("(empty)"));
            }
            return root;
        }

        else if (node instanceof ASTNode.BlockNode blockNode) {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("{Block}");
            for (ASTNode stmt : blockNode.statements) {
                root.add(buildTreeNode(stmt));
            }
            return root;
        }

        return new DefaultMutableTreeNode("(Unknown Node)");
    }
}
