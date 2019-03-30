import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

public class ScannerDebug {
	static PushbackInputStream input;
	static Lexeme current_lexeme;
	static Lexer lx;

	/*Helper functions*/
	public static void advance() throws IOException {
		current_lexeme = lx.lex();
	}
	public static boolean check(String type){
		if (current_lexeme.type.equals(type)){
			return true;
		}
		return false;
	}
	public static void match(String type) throws IOException {
		matchNoAdvance(type);
		advance();
	}
	public static void matchNoAdvance(String type){
		if(!check(type)){
			System.out.println("Syntax Error: on line #" + current_lexeme.lineNumber);
		}
	}
	/*Driver functions*/
	public static void recognize(String fileName) throws IOException {
		File f = new File(fileName);
		input = new PushbackInputStream(new FileInputStream(f));
		lx = new Lexer(input);
		advance();
		while(!(current_lexeme.type.equals("EOF"))){
			advance();
			System.out.printf("%-20s\t%-20s\t%-20d\n",current_lexeme.type,current_lexeme.value, current_lexeme.lineNumber);
		}
	}
	public static void main(String[] args) throws IOException {
		recognize(args[0]);
	}
}