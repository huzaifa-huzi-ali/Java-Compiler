# Java-Compiler
A Java-based mini compiler with a graphical interface that performs lexical analysis, parsing, semantic checks, constant folding optimization, and intermediate code generation for basic programming constructs like variables, loops, conditionals, and arithmetic expressions.
# 🧠 Mini Compiler in Java

A GUI-based mini compiler developed in Java that compiles a simplified subset of the Java language. It supports core programming constructs such as variables, data types, arithmetic operations, conditional statements (`if/else`), and loops (`for`, `while`). This project was developed as part of the Data Structures and Algorithms (DSA) course.

---

## 📌 Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [System Architecture](#system-architecture)
- [Class Breakdown](#class-breakdown)
- [GUI Functionality](#gui-functionality)
- [How to Run](#how-to-run)
- [Screenshots](#screenshots)
- [Future Enhancements](#future-enhancements)
- [Authors](#authors)

---

## ✅ Features

- **Lexical Analysis** – Breaks down the input source code into tokens.
- **Parsing** – Constructs an Abstract Syntax Tree (AST) from the tokens.
- **Semantic Analysis** – Validates types and variable declarations.
- **Optimization** – Applies constant folding to simplify expressions at compile time.
- **Intermediate Code Generation** – Produces 3-address code with temporary variables and labels.
- **Swing-based GUI** – Allows users to input code, view outputs, and toggle dark mode.

---

## 💻 Technologies Used

- Java (JDK 8+)
- Swing (GUI Toolkit)

---

## 🧱 System Architecture

The mini compiler follows a **multi-phase architecture**:

1. **Lexical Analysis** – Tokenizes source code using `ManualLexer`.
2. **Syntax Analysis** – Builds AST using `ManualParser`.
3. **Semantic Analysis** – Type checking using `SemanticAnalyzer`.
4. **Optimization** – Constant folding with `ConstantFolder`.
5. **Code Generation** – Produces intermediate code via `IntermediateCodeGenerator`.

---

## 🧩 Class Breakdown

### `Main.java` – GUI Interface
- Swing-based text editor
- Tabs for Tokens, AST, and Intermediate Code
- Dark mode toggle
- Full pipeline execution

### `ManualLexer.java` – Lexical Analyzer
- Converts code into tokens (keywords, operators, literals)
- Handles lookahead and matching patterns

### `ManualParser.java` – Syntax Analyzer
- Builds AST from tokens
- Supports constructs like `if`, `while`, `for`, expressions

### `ASTNode.java` – Abstract Syntax Tree
- Represents parsed code structure
- Node types: `IfNode`, `ForNode`, `BinOpNode`, etc.

### `SemanticAnalyzer.java`
- Type checking and variable validation
- Symbol table management

### `ConstantFolder.java`
- Performs compile-time constant folding
- Simplifies expressions like `3 * 5 → 15`

### `IntermediateCodeGenerator.java`
- Produces three-address code (TAC)
- Uses temporaries (`t0`, `t1`) and labels (`L0`, `L1`)

---

## 🖥 GUI Functionality

| Feature            | Description                                   |
|--------------------|-----------------------------------------------|
| Load File          | Import `.txt` source code                     |
| Show Tokens        | Display tokenized output                      |
| Show AST           | Render AST in a JTree                         |
| Semantic Analysis  | Run type checking                             |
| Generate Code      | Show intermediate code                        |
| Full Pipeline      | Run all compilation phases                    |
| Save Output        | Export results                                |
| Dark Mode          | Toggle light/dark themes                      |

---

## 🚀 How to Run

1. **Compile the Project**
   ```bash
   javac *.java
   java Main
