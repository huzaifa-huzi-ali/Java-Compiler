package parser;

import java.util.ArrayList;
import java.util.List;

// ManualParser class is responsible for parsing tokens into an Abstract Syntax Tree (AST)
public class ManualParser {
    private final ManualLexer lexer;       // Lexer to tokenize the input
    private Token currentToken;            // The current token being processed

    // Constructor initializes the parser with a lexer and initialize the first token
    public ManualParser(ManualLexer lexer) {
        this.lexer = lexer;
        this.currentToken = lexer.getNextToken();  // Read the first token
    }

    // Utility method to match and consume the expected token type.
    // Throws an error if the token doesn't match.
    private void get(TokenType type) {
        if (currentToken.type == type) {
            currentToken = lexer.getNextToken(); // Move to next token
        } else {
            throw new RuntimeException("Unexpected token: " + currentToken + " at position " + lexer.getPosition());
        }
    }

    // Parses a variable declaration and assignment, e.g., int x = 10;
    private ASTNode parseAssignment(boolean expectSemicolon) {
        String DecType = null;

        if (currentToken.type == TokenType.INT_KEYWORD ||
                currentToken.type == TokenType.FLOAT_KEYWORD ||
                currentToken.type == TokenType.STRING_KEYWORD) {
            DecType = currentToken.value;
            get(currentToken.type);
        }

        String varName = currentToken.value;
        get(TokenType.IDENTIFIER);
        get(TokenType.ASSIGN);

        if (currentToken.type == TokenType.SEMICOLON) {
            throw new RuntimeException("Missing expression after '=' before semicolon.");
        }

        ASTNode value = parseExpression();

        if (expectSemicolon) {
            get(TokenType.SEMICOLON);
        }

        return new ASTNode.AssignNode(varName, value, DecType);
    }


    // Parses expressions (e.g: a + b, x > 5)
    private ASTNode parseExpression() {
        ASTNode left = parseTerm();

        // Handle operators: ==, !=, <, >, <=, >=, +, -
        while (currentToken.type == TokenType.EQ ||
                currentToken.type == TokenType.NE ||
                currentToken.type == TokenType.LT ||
                currentToken.type == TokenType.GT ||
                currentToken.type == TokenType.LE ||
                currentToken.type == TokenType.GE ||
                currentToken.type == TokenType.PLUS ||
                currentToken.type == TokenType.MINUS) {

            String op = currentToken.value;
            get(currentToken.type);               // Consume operator
            ASTNode right = parseTerm();          // Parse right-hand side term
            left = new ASTNode.BinOpNode(left, op, right);  // Build binary operation node
        }

        return left;
    }

    // Parses multiplication/division parts of an expression
    private ASTNode parseTerm() {
        ASTNode node = parseFactor();

        // Handle '*' and '/' operators
        while (currentToken.type == TokenType.MUL || currentToken.type == TokenType.DIV) {
            String op = currentToken.value;
            get(currentToken.type);               // Consume operator
            ASTNode right = parseFactor();        // Parse right-hand side factor
            node = new ASTNode.BinOpNode(node, op, right);  // Build binary operation node
        }
        return node;
    }

    // Parses basic units of expressions: literals, identifiers, and grouped expressions
    private ASTNode parseFactor() {
        Token token = currentToken;

        switch (token.type) {
            case INT_LITERAL:
                get(TokenType.INT_LITERAL);
                return new ASTNode.IntNode(Integer.parseInt(token.value));

            case FLOAT_LITERAL:
                get(TokenType.FLOAT_LITERAL);
                return new ASTNode.FloatNode(Double.parseDouble(token.value));

            case STRING_LITERAL:
                get(TokenType.STRING_LITERAL);
                return new ASTNode.StringNode(token.value);

            case LPAREN:
                get(TokenType.LPAREN);
                ASTNode node = parseExpression();  // Parse nested expression
                get(TokenType.RPAREN);
                return node;

            case IDENTIFIER:
                get(TokenType.IDENTIFIER);
                return new ASTNode.VarNode(token.value);

            default:
                throw new RuntimeException("Unexpected token in expression: " + token);
        }
    }

    // Parses a block of statements enclosed in braces: { ... }
    private ASTNode parseBlock() {
        get(TokenType.LBRACE);                   // Opening brace
        List<ASTNode> statements = new ArrayList<>();

        while (currentToken.type != TokenType.RBRACE) {
            statements.add(parseStatement());   // Parse each statement
        }

        get(TokenType.RBRACE);                  // Closing brace
        return new ASTNode.BlockNode(statements);
    }

    // Parses an if-else statement
    private ASTNode parseIf() {
        get(TokenType.IF);
        get(TokenType.LPAREN);
        ASTNode condition = parseExpression();   // Parse condition
        get(TokenType.RPAREN);
        ASTNode thenBlock = parseBlock();        // Parse "then" block

        ASTNode elseBlock = null;
        if (currentToken.type == TokenType.ELSE) {
            get(TokenType.ELSE);
            elseBlock = parseBlock();            // Optional "else" block
        }

        return new ASTNode.IfNode(condition, thenBlock, elseBlock);
    }

    // Parses a for loop: for (init; condition; update) { body }
    private ASTNode parseFor() {
        get(TokenType.FOR);
        get(TokenType.LPAREN);
        ASTNode init = parseAssignment(true);         // Initialization
        ASTNode condition = parseExpression();    // Condition
        get(TokenType.SEMICOLON);
        ASTNode update = parseAssignment(false);       // Update expression
        get(TokenType.RPAREN);
        ASTNode body = parseBlock();              // Loop body

        return new ASTNode.ForNode(init, condition, update, body);
    }

    // Parses a while loop: while (condition) { body }
    private ASTNode parseWhile() {
        get(TokenType.WHILE);
        get(TokenType.LPAREN);
        ASTNode condition = parseExpression();    // Condition
        get(TokenType.RPAREN);
        ASTNode body = parseBlock();              // Loop body

        return new ASTNode.WhileNode(condition, body);
    }

    // Parses a return statement with expression
    private ASTNode parseReturn() {
        get(TokenType.RETURN);

        ASTNode expr = null;
        if (currentToken.type != TokenType.SEMICOLON) {
            expr = parseExpression();    // Optional return expression
        }

        get(TokenType.SEMICOLON);
        return new ASTNode.ReturnNode(expr);
    }

    // Decides which parser method to use based on current token type
    public ASTNode parseStatement() {
        if (currentToken.type == TokenType.IF) {
            return parseIf();
        } else if (currentToken.type == TokenType.FOR) {
            return parseFor();
        } else if (currentToken.type == TokenType.WHILE) {
            return parseWhile();
        } else if (currentToken.type == TokenType.RETURN) {
            return parseReturn();
        } else if (currentToken.type == TokenType.LBRACE) {
            return parseBlock();
        } else {
            return parseAssignment(true);  // Default treat as an assignment statement
        }
    }

    // Parses the whole input program until EOF
    public List<ASTNode> parseProgram() {
        List<ASTNode> statements = new ArrayList<>();

        while (currentToken.type != TokenType.EOF) {
            statements.add(parseStatement());  // Add parsed statement to list
        }

        return statements;  // Return full AST for the program
    }
}
