import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

public class Lexer {
	static PushbackInputStream input;
	static Integer lineNumber;
	Lexer (PushbackInputStream i){
		input = i;
		lineNumber = 1;
	}
	public static Lexeme skipComment() throws IOException {
		int data = input.read();
		Character ch;
		while (data != -1) {
			ch = (char) data;
			if (ch == '\n') lineNumber++;
			else if (ch == '*') {
				data = input.read();
				if (data != -1 && (char) data == '/') {
					return new Lexeme("COMMENT","COMMENT",lineNumber);
				}
			}
			data = input.read();
		}
		return new Lexeme("EOF","EOF",lineNumber);
	}
	public static Lexeme skipWhiteSpace() throws IOException {
		int data = input.read();
		Character ch;
		while (data != -1) {
			ch = (char) data;
			if (Character.isWhitespace(ch) == false) {
				input.unread(data);
				return new Lexeme("WHITESPACE","WHITESPACE",lineNumber);
			}
			if (ch == '\n') lineNumber++;
			data = input.read();
		}
		return new Lexeme("EOF","EOF",lineNumber);
	}
	
	public static Lexeme lexString() throws IOException {
		int data = input.read();
		Character ch;
		String buffer = "";
		while (data != -1) {
			ch = (char) data;
			if (ch == '\"') { //close quotations
				return new Lexeme("STRING",buffer,lineNumber);
			}
			buffer = buffer.concat(ch.toString());
			data = input.read();
		}
		return new Lexeme("BAD_STRING",buffer,lineNumber);
	}
	
	public static Lexeme lexNumber() throws IOException {
		int data = input.read();
		Character ch;
		String buffer = "";
		boolean real = false;
		while (data != -1) {
			ch = (char) data;
			if (Character.isDigit(ch) || ch == '.') { //any thing other than a number
				buffer = buffer.concat(ch.toString());
				if (ch == '.' && real == true) return new Lexeme("BAD_NUMBER", buffer, lineNumber);
				else if (ch == '.') real = true;
			}
			else {
				break;
			}
			data = input.read();
		}
		input.unread(data);
		if (real) return new Lexeme("REAL",buffer,lineNumber);
		return new Lexeme("INTEGER", buffer, lineNumber);
	}
	
	public static Lexeme lexKeyWord(String buffer) throws IOException {
		if (buffer.equals("var")) {
			return new Lexeme("VAR","var",lineNumber) ;
		}
		return null;
	}
	
	public static Lexeme lexToken() throws IOException {
		int data = input.read();
		Character ch;
		String buffer = "";
		while (data != -1) {
			ch = (char) data;
			if (!(Character.isLetter(ch)) && !(Character.isDigit(ch)) && (ch != '_')) {
				input.unread(data);
				/*Keywords*/
				if (buffer.equals("import")) {
					return new Lexeme("IMPORT",buffer,lineNumber);
				}
				else if (buffer.equals("var")) {
					return new Lexeme("VAR",buffer,lineNumber);
				}
				else if (buffer.equals("class")) {
					return new Lexeme("CLASS",buffer,lineNumber);
				}
				else if (buffer.equals("return")) {
					return new Lexeme("RETURN",buffer,lineNumber);
				}
				else if (buffer.equals("while")) {
					return new Lexeme("WHILE",buffer,lineNumber);
				}
				else if (buffer.equals("if")) {
					return new Lexeme("IF",buffer,lineNumber);
				}
				else if (buffer.equals("else")) {
					return new Lexeme("ELSE",buffer,lineNumber);
				}
				else if (buffer.equals("true")) {
					return new Lexeme("BOOLEAN",buffer,lineNumber);
				}
				else if (buffer.equals("false")) {
					return new Lexeme("BOOLEAN",buffer,lineNumber);
				}
				else if (buffer.equals("function")) {
					return new Lexeme("FUNCTION",buffer,lineNumber);
				}
				else {
					return new Lexeme("ID",buffer,lineNumber);
				}
			}
			buffer = buffer.concat(ch.toString());
			data = input.read();
		}
		input.unread(data);
		return null;
	}
	
	public static Lexeme lex() throws IOException {
		if (skipWhiteSpace().type == "EOF") return new Lexeme("EOF","EOF",lineNumber) ;  
		int data = input.read();
		Character ch;
		if (data == -1) return null; // EOF shouldn't actually hit here
		ch = (char) data;
		/*Single Character*/
			/*Punctuations*/
		if (ch == '{') {
			return new Lexeme("OBRACE","{",lineNumber) ;
		}
		else if (ch == '}') {
			return new Lexeme("CBRACE","}",lineNumber) ;
		}
		else if (ch == '(') {
			return new Lexeme("OPAREN","(",lineNumber) ;
		}
		else if (ch == ')') {
			return new Lexeme("CPAREN",")",lineNumber) ;
		}
		else if (ch == '[') {
			return new Lexeme("OBRACKET","[",lineNumber) ;
		}
		else if (ch == ']') {
			return new Lexeme("CBRACKET","]",lineNumber) ;
		}
		else if (ch == ';') {
			return new Lexeme("SEMI",";",lineNumber) ;
		}
		else if (ch == '.') {
			return new Lexeme("DOT",".",lineNumber) ;
		}
		else if (ch == ',') {
			return new Lexeme("COMMA",",",lineNumber) ;
		}
		else if (ch == '!') {
			return new Lexeme("EXCLAIM","!",lineNumber) ;
		}
			/*Operators*/
		else if (ch == '+') {
			return new Lexeme("PLUS","+",lineNumber) ;
		}
		else if (ch == '*') {
			return new Lexeme("TIMES","*",lineNumber) ;
		}
		else if (ch == '-') {
			return new Lexeme("MINUS","-",lineNumber) ;
		}
		else if (ch == '/') { // comment
			data = input.read();
			ch = (char) data;
			if (ch == '*') {
				return skipComment();				
			}
			else {
				input.unread(data);
				return new Lexeme("DIVIDE","/",lineNumber) ;
			}
		}
		else if (ch == '=') { // might have to do an equals equals
			return new Lexeme("EQUALS","=",lineNumber) ;
		}
		else if (ch == '>') {
			return new Lexeme("GREATER",">",lineNumber) ;
		}
		else if (ch == '<') {
			return new Lexeme("FEWER","<",lineNumber) ;
		}
			/* Misc */
		else if (ch == '\"') {
			return lexString();
		}
		else if (Character.isDigit(ch)) {
			input.unread(data);
			return lexNumber();			
		}
		else if (Character.isLetter(ch)) {
			input.unread(data);
			return lexToken();
		}
		return new Lexeme("BAD_CHAR",ch.toString(),lineNumber);
	}
}