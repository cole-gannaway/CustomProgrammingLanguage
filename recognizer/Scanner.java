import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

public class Scanner {
	static PushbackInputStream input;
	static Lexeme current_lexeme;
	static Lexer lx;




/*Helper functions*/
	public static Lexeme advance() throws IOException {
		Lexeme prev_lexeme = current_lexeme;
		current_lexeme = lx.lex();
		return prev_lexeme;
	}
	public static boolean check(String type){
		if (current_lexeme.type.equals(type)){
			return true;
		}
		return false;
	}
	public static Lexeme match(String type) throws IOException {
		if (check(type)) return advance();
		try {
			matchError();
		}
		catch (IOException e){
   			System.out.println("exception handled");
   		}
	}
	public static void matchError(){
		System.out.printf("\nLexeme errored on line #%d\t%-20s\t%-20s\n",current_lexeme.lineNumber,current_lexeme.type,current_lexeme.value);
		System.out.printf("\033[0;31m"); //Set the text to the color red
		System.out.printf("\033[31m----Syntax Error: on line #%d\t%-20s\t%-20s\t%-20s\n",current_lexeme.lineNumber,("Expected: "+ type),("Got type:" + current_lexeme.type),current_lexeme.value);
		System.out.printf("\033[0m"); //Resets the text to default color
		throw(new IOException("illegal"));
	}
	



/*Grammar functions*/

	public static void program() throws IOException {
		opt_aux_file();
		class_definition();
		return;
	}
	public static void opt_aux_file() throws IOException {
		if (aux_file_pending() == true){
			aux_file();
			match("SEMI");
		}
	}
	public static void aux_file() throws IOException {
		match("IMPORT");
		identifier_sequence();
	}
	public static boolean aux_file_pending() throws IOException {
		return check("IMPORT");
	}
	public static void identifier_sequence() throws IOException {
		match("ID");
		if (check("DOT")){
			match("DOT");
			identifier_sequence();
		}
	}
	public static boolean identifier_sequence_pending() throws IOException {
		return check("ID");
	}	
	public static void class_definition() throws IOException {
		match("CLASS");
		match("ID");
		match("OBRACE");
		definition_sequence();
		match("CBRACE");
	}
	public static void definition_sequence() throws IOException {
		definition();
		if (definition_sequence_pending()) {
			definition_sequence();
		}
	}
	public static boolean definition_sequence_pending() throws IOException {
		return definition_pending();
	}
	public static void definition() throws IOException {
		if (variable_def_pending()){
			variable_def();
		}
		else if (method_def_pending()){
			method_def();
		}
	}
	public static boolean definition_pending() throws IOException {
		return check("VAR") || check("FUNCTION");
	}
	public static void variable_def() throws IOException {
		match("VAR");
		match("ID");
		if (optInit_pending()){
			optInit();
		}
		match("SEMI");
	}
	public static boolean variable_def_pending() throws IOException {
		return check("VAR");
	}
	public static void optInit() throws IOException {
		match("EQUALS");
		expression();
	}
	public static boolean optInit_pending() throws IOException {
		return check("EQUALS");
	}
	public static void method_def() throws IOException {
		match("FUNCTION");
		match("ID");
		match("OPAREN");
		opt_parameter_sequence();
		match("CPAREN");
		block();
	}
	public static boolean method_def_pending() throws IOException {
		return check("FUNCTION");
	}
	public static void opt_parameter_sequence() throws IOException {
		if (parameter_sequence_pending()){
			parameter_sequence();
		}
	}
	public static void parameter_sequence() throws IOException {
		unary();
		if (check("COMMA")){
			match("COMMA");
			parameter_sequence();
		}
	}
	public static boolean parameter_sequence_pending() throws IOException {
		return unary_pending();
	}
	public static void block() throws IOException {
		match("OBRACE");
		statement_sequence();
		match("CBRACE");
	}
	public static boolean block_pending() throws IOException {
		return check("OBRACE");
	}
	public static void statement_sequence() throws IOException {
		statement();
		if (statement_sequence_pending()){
			statement_sequence();
		}
	}
	public static boolean statement_sequence_pending() throws IOException {
		return statement_pending();
	}
	public static void statement() throws IOException {
		if (expression_pending()){
			expression();
			match("SEMI");
		}
		else if (variable_def_pending()){
			variable_def();
		}
		else if (return_statement_pending()){
			return_statement();
		}
		else if (if_statement_pending()){
			if_statement();
		}
		else if (while_statement_pending()){
			while_statement();
		}
	}
	public static boolean statement_pending() throws IOException {
		if (expression_pending()){
			return true;
		}
		else if (variable_def_pending()){
			return true;
		}
		else if (return_statement_pending()){
			return true;
		}
		else if (if_statement_pending()){
			return true;
		}
		else if (while_statement_pending()){
			return true;
		}
		return false;
	}
	public static void return_statement() throws IOException {
		match("RETURN");
		expression();
		match("SEMI");
	}
	public static boolean return_statement_pending() throws IOException {
		return check("RETURN");
	}
	public static void if_statement() throws IOException {
		match("IF");
		match("OPAREN");
		expression();	
		match("CPAREN");
		block();
		optElse();
	}
	public static boolean if_statement_pending() throws IOException {
		return check("IF");
	}
	public static void optElse() throws IOException {
		if (check("ELSE")){
			match("ELSE");
			else_statement();
		}
	}
	public static boolean optElse_pending() throws IOException {
		return check("ELSE");
	}
	public static void else_statement() throws IOException {
		if (if_statement_pending()) {
			if_statement();
		}
		else if (block_pending()){
			block();
		}
	}
	public static void while_statement() throws IOException {
		match("WHILE");
		match("OPAREN");
		expression();
		match("CPAREN");
		block();
	}
	public static boolean while_statement_pending() throws IOException {
		return check("WHILE");
	}
	public static void expression() throws IOException {
		unary();
		if (operator_sequence_pending()){
			operator_sequence();
			expression();
		}
	}
	public static boolean expression_pending() throws IOException {
		return unary_pending();
	}
	public static void operator_sequence() throws IOException {
		operator();
		if (operator_sequence_pending()){
			operator_sequence();
		}
	}
	public static boolean operator_sequence_pending() throws IOException {
		return operator_pending();
	}
	public static void operator() throws IOException {
		if (check("DIVIDE")){
			match("DIVIDE");
		}
		else if (check("EQUALS")){
			match("EQUALS");
		}
		else if (check("EXCLAIM")){
			match("EXCLAIM");
		}
		else if (check("FEWER")){
			match("FEWER");
		}
		else if (check("GREATER")){
			match("GREATER");
		}
		else if (check("MINUS")){
			match("MINUS");
		}
		else if (check("PLUS")){
			match("PLUS");
		}
		else if (check("TIMES")){
			match("TIMES");
		}
	}
	public static boolean operator_pending() throws IOException {
		if (check("DIVIDE")){
			return true;
		}
		else if (check("EQUALS")){
			return true;
		}
		else if (check("EXCLAIM")){
			return true;
		}
		else if (check("FEWER")){
			return true;
		}
		else if (check("GREATER")){
			return true;
		}
		else if (check("MINUS")){
			return true;
		}
		else if (check("PLUS")){
			return true;
		}
		else if (check("TIMES")){
			return true;
		}
		return false;
	}
	public static void unary() throws IOException {
		if (check("INTEGER")){
			match("INTEGER");
		}
		else if (check("REAL")){
			match("REAL");
		}
		else if (check("BOOLEAN")){
			match("BOOLEAN");
		}
		else if (check("CHARACTER")){
			match("CHARACTER");
		}
		else if (check("STRING")){
			match("STRING");
		}
		else if (check("OPAREN")){
			match("OPAREN");
			expression();
			match("CPAREN");
		}
		else if (check("MINUS")){
			match("MINUS");
			unary();
		}
		else if (identifier_sequence_pending()){
			identifier_sequence();
			opt_method_call();
		}
	}
	public static boolean unary_pending() throws IOException {
		if (check("INTEGER")){
			return true;
		}
		else if (check("REAL")){
			return true;
		}
		else if (check("BOOLEAN")){
			return true;
		}
		else if (check("CHARACTER")){
			return true;
		}
		else if (check("STRING")){
			return true;
		}
		else if (check("OPAREN")){
			return true;
		}
		else if (check("MINUS")){
			return true;
		}
		else if (identifier_sequence_pending()){
			return true;
		}
		return false;
	}
	public static void opt_method_call() throws IOException {
		if (method_call_pending()){
			method_call();
		}
	}
	public static void method_call() throws IOException {
		match("OPAREN");
		opt_argument_sequence();
		match("CPAREN");	
	}
	public static boolean method_call_pending() throws IOException {
		return check("OPAREN");
	}
	public static void opt_argument_sequence() throws IOException {
		if (argument_sequence_pending()){
			argument_sequence();
		}
	}
	public static void argument_sequence() throws IOException {
		expression();
		if (check("COMMA")) {
			match("COMMA");
			argument_sequence();
		}
	}
	public static boolean argument_sequence_pending() throws IOException {
		return expression_pending();
	}
	
/*Template
	public static void ________________() throws IOException {
	}
	public static boolean ________________() throws IOException {
	}
*/



/*Driver functions*/
	
	public static void recognize(String fileName) throws IOException {
		File f = new File(fileName);
		input = new PushbackInputStream(new FileInputStream(f));
		lx = new Lexer(input);
		advance();
		program();
		System.out.println("legal");
	}
	public static void main(String[] args) throws IOException {
		recognize(args[0]);
	}
}