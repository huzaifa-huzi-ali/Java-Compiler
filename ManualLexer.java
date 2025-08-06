package parser;

/*
   ManualLexer is a simple tokenizer (lexer) that reads characters from an input string
   and returns tokens one by one. It handles keywords, identifiers, literals, operators,
   punctuation, and skips whitespace and comments.
 */
public class ManualLexer {
    private final String input;  // Source code input as a string
    private int pos;             // Current position in the input
    private final int length;    // Total length of the input


    // Constructor that initializes the lexer with the input source code
    public ManualLexer(String input) {
        this.input = input;
        this.length = input.length();
        this.pos = 0;
    }

    /*
       Tries to match a specific string at the current position.
       If matched, it advances the position.
     */
    private boolean match(String expected) {
        int expectedLength = expected.length();
        if (pos + expectedLength <= length && input.substring(pos, pos + expectedLength).equals(expected)) {
            pos += expectedLength;
            return true;
        }
        return false;
    }

    /*
       Main method to retrieve the next token from the input stream.
       Handles all kinds of tokens: identifiers, numbers, keywords, operators, etc.
     */
    public Token getNextToken() {
        while (pos < length) {
            char current = input.charAt(pos);

            // Skip whitespace characters (spaces, tabs, newlines)
            if (Character.isWhitespace(current)) {
                pos++;
                continue;
            }

            // Handle single-line comments like // comment
            if (current == '/' && peek() == '/') {
                pos += 2;  // Skip both slashes
                while (pos < length && input.charAt(pos) != '\n') {
                    pos++;  // Skip until end of line
                }
                continue;
            }

            // Handle multi-line comments like /* comment */
            if (current == '/' && peek() == '*') {
                pos += 2;  // Skip the /*
                boolean closed = false;
                while (pos < length - 1) {
                    if (input.charAt(pos) == '*' && input.charAt(pos + 1) == '/') {
                        pos += 2;  // Skip the */
                        closed = true;
                        break;
                    }
                    pos++;
                }
                if (!closed) {
                    return new Token(TokenType.UNKNOWN, "Unterminated comment");
                }
                continue;
            }

            // Handle identifiers and keywords
            if (Character.isLetter(current)) {
                StringBuilder sb = new StringBuilder();
                while (pos < length && Character.isLetterOrDigit(input.charAt(pos))) {
                    sb.append(input.charAt(pos++));
                }
                String word = sb.toString();
                // Check for keywords; otherwise, it's an identifier
                switch (word) {
                    case "int": return new Token(TokenType.INT_KEYWORD, word);
                    case "float": return new Token(TokenType.FLOAT_KEYWORD, word);
                    case "string": return new Token(TokenType.STRING_KEYWORD, word);
                    case "if": return new Token(TokenType.IF, word);
                    case "else": return new Token(TokenType.ELSE, word);
                    case "for": return new Token(TokenType.FOR, word);
                    case "while": return new Token(TokenType.WHILE, word);
                    case "return": return new Token(TokenType.RETURN, word);
                    default: return new Token(TokenType.IDENTIFIER, word);
                }
            }

            // Handle comparison operators (==, !=, <=, >=, <, >)
            if (match("==")) return new Token(TokenType.EQ, "==");
            if (match("!=")) return new Token(TokenType.NE, "!=");
            if (match("<=")) return new Token(TokenType.LE, "<=");
            if (match(">=")) return new Token(TokenType.GE, ">=");
            if (match("<")) return new Token(TokenType.LT, "<");
            if (match(">")) return new Token(TokenType.GT, ">");

            // Handle integer and float numbers
            if (Character.isDigit(current)) {
                StringBuilder number = new StringBuilder();
                boolean hasDot = false;
                while (pos < length && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) {
                    if (input.charAt(pos) == '.') {
                        if (hasDot) {
                            break;   // Only allow one dot
                        }
                        hasDot = true;
                    }
                    number.append(input.charAt(pos++));
                }
                return hasDot ? new Token(TokenType.FLOAT_LITERAL, number.toString()) : new Token(TokenType.INT_LITERAL, number.toString());
            }

            // Handle string literals like "Hello"
            if (current == '"') {
                pos++;  // Skip the opening quote
                StringBuilder sb = new StringBuilder();
                while (pos < length && input.charAt(pos) != '"') {
                    sb.append(input.charAt(pos++));
                }
                if (pos >= length) {
                    throw new RuntimeException("Unterminated string literal");
                }
                pos++;  // Skip the closing quote
                return new Token(TokenType.STRING_LITERAL, sb.toString());
            }

            // Handle single-character operators and symbols
            switch (current) {
                case '=': pos++; return new Token(TokenType.ASSIGN, "=");
                case '+': pos++; return new Token(TokenType.PLUS, "+");
                case '-': pos++; return new Token(TokenType.MINUS, "-");
                case '*': pos++; return new Token(TokenType.MUL, "*");
                case '/': pos++; return new Token(TokenType.DIV, "/");
                case '(': pos++; return new Token(TokenType.LPAREN, "(");
                case ')': pos++; return new Token(TokenType.RPAREN, ")");
                case ';': pos++; return new Token(TokenType.SEMICOLON, ";");
                case ',': pos++; return new Token(TokenType.COMMA, ",");
                case '{': pos++; return new Token(TokenType.LBRACE, "{");
                case '}': pos++; return new Token(TokenType.RBRACE, "}");
            }

            // If none of the above match, return an unknown token
            return new Token(TokenType.UNKNOWN, String.valueOf(input.charAt(pos++)));
        }

        // Reached the end of the input
        return new Token(TokenType.EOF, "");
    }


    // Looks ahead one character without advancing the position
    private char peek() {
        if (pos + 1 >= length) {
            return '\0';  // Return null char if out of bounds
        }
        return input.charAt(pos + 1);
    }

    // Returns the current position in the input string
    public int getPosition() {
        return pos;
    }
}
