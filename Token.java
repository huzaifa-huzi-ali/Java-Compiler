package parser;

/*
   Token class represents a single token from the source code.
   Each token has a type (defined in TokenType enum) and a string value.
   This class is used by the lexer to pass tokens to the parser.
 */
public class Token {

    // Type of the token (e.g., IDENTIFIER, INT_LITERAL, PLUS, etc.)
    public TokenType type;

    // Actual string value of the token (e.g., "x", "42", "+", etc.)
    public String value;


    // Constructor for creating a Token with a specific type and value
    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }


    // Returns a human-readable string representation of the token
    @Override
    public String toString() {
        return String.format("Token(type=%s, value='%s')", type, value);
    }
}
