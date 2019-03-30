import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

public class Scanner {
	static PushbackInputStream input;
	static Lexeme current_lexeme;
	static Lexer lx;



/*Helper functions*/

	public static void advance() throws IOException {
		current_lexeme = lx.lex();
		System.out.printf("\nLexeme is now from line #%d\t%-20s\t%-20s\n",current_lexeme.lineNumber,current_lexeme.type,current_lexeme.value);
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

			System.out.printf("\nLexeme is now from line #%d\t%-20s\t%-20s\n",current_lexeme.lineNumber,current_lexeme.type,current_lexeme.value);
			System.out.printf("\033[0;31m"); //Set the text to the color red
			System.out.printf("\033[31m----Syntax Error: on line #%d\t%-20s\t%-20s\t%-20s\n",current_lexeme.lineNumber,("Expected: "+ type),("Got type:" + current_lexeme.type),current_lexeme.value);
			System.out.printf("\033[0m"); //Resets the text to default color
			
		}
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
		block_sequence();
		match("CBRACE");
	}
	public static void block_sequence() throws IOException {



		System.out.println("block_sequence()");



		block_element();
		if (block_sequence_pending()) {
			block_sequence();
		}
	}
	public static boolean block_sequence_pending() throws IOException {
		if (method_def_pending()){
			return true;
		}
		else if (method_call_pending()){
			return true;
		}
		else if (variable_def_pending()){
			return true;
		}
		else if (variable_assign_pending()){
			return true;	
		}
		else if (conditional_pending()){
			return true;
		}
		else if (loop_pending()){
			return true;
		}
		else if (expression_pending()){
			return true;
		}
		return false;
	}
	public static void block_element() throws IOException {
		if (method_def_pending()){
			method_def();
		}
		else if (method_call_pending()){
			method_call();
			match("SEMI");
		}
		else if (variable_def_pending()){
			variable_def();
		}
		else if (variable_assign_pending()){
			variable_assign();
		}
		else if (conditional_pending()){
			conditional();
		}
		else if (loop_pending()){
			loop();
		}
		else if (expression_pending()){
			expression();
			match("SEMI");
		}
	}
	public static void method_def() throws IOException {
		match("FUNCTION");
		match("ID");
		match("OPAREN");
		opt_parameter_sequence();
		match("CPAREN");
		match("OBRACE");
		block_sequence();
		match("RETURN");
		expression();
		match("SEMI");
		match("CBRACE");
	}
	public static boolean method_def_pending() throws IOException {
		return check("FUNCTION");
	}
	public static void method_call() throws IOException {
		identifier_sequence();
		match("OPAREN");
		opt_argument_sequence();
		match("CPAREN");
	}
	public static boolean method_call_pending() throws IOException {

		System.out.println("method_call_pending()");


		
		return identifier_sequence_pending();
	}
	public static void variable_def() throws IOException {
		match("VAR");
		match("ID");
		if (check("EQUALS")) {
			match("EQUALS");
			expression();
			match("SEMI");
		}
		else if (check("SEMI")){
			match("SEMI");
		}
		else if (array_def_pending()){
			array_def();
		}
	}
	public static boolean variable_def_pending() throws IOException {
		return check("VAR");
	}
	public static void variable_assign() throws IOException {

		

		System.out.println("variable_assign()");



		match("ID");
		match("EQUALS");
		expression();
		match("SEMI");
	}
	public static boolean variable_assign_pending() throws IOException {
		return check("ID");
	}
	public static void expression() throws IOException {


		System.out.println("expression()");



		unary();
		if (operator_sequence_pending()){
			operator_sequence();
			expression();
		}
	}
	public static boolean expression_pending() throws IOException {
		return unary_pending();
	}
	public static void unary() throws IOException {


		System.out.println("unary()");



		if (check("ID")) {
			match("ID");
		}
		else if (check("INTEGER")){
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
		else if (method_call_pending()){
			method_call();
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
		}
		else if (array_access_pending()){
			array_access();
		}
	}
	public static boolean unary_pending() throws IOException {
		if (check("ID")) {
			return true;
		}
		else if (check("INTEGER")){
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
		else if (method_call_pending()){
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
		else if (array_access_pending()){
			return true;
		}
		return false;
	}
	public static void operator_sequence() throws IOException {

		System.out.println("operator_sequence()");

		operator();
		if (operator_sequence_pending()){
			operator_sequence();
		}
	}
	public static boolean operator_sequence_pending() throws IOException {
		System.out.println("operator_sequence_pending()");
		return operator_pending();
	}
	public static void operator() throws IOException {

		
		System.out.println("operator()");



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


		System.out.println("operator_pending()");

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
	public static void conditional() throws IOException {
		if (check("IF")) {
			match("IF");
			unary();
			match("OBRACE");
			block_sequence();
			match("CBRACE");
		}
		else if (check("ELSE")) {
			match("ELSE");
			if (conditional_pending()) {
				conditional();
			}
			else if (check("OBRACE")){
				match("OBRACE");
				block_sequence();
				match("CBRACE");
			}
		}
	}
	public static boolean conditional_pending() throws IOException {
		if (check("IF")){
			return true;
		}
		else if (check("ELSE")){
			return true;
		}
		return false;
	}
	public static void loop() throws IOException {

		System.out.println("loop()");


		match("WHILE");
		match("OPAREN");
		expression();
		match("CPAREN");
		match("OBRACE");
		block_sequence();
		match("CBRACE");

	}
	public static boolean loop_pending() throws IOException {
		return check("WHILE");
	}
	public static void array_def() throws IOException {
		match("VAR");
		match("ID");
		match("OBRACKET");
		expression();
		match("CBRACKET");
		match("SEMI");
	}
	public static boolean array_def_pending() throws IOException {
		return check("VAR");
	}
	public static void array_access() throws IOException {
		match("ID");
		array_sequence();
	}
	public static boolean array_access_pending() throws IOException {
		return check("ID");
	}
	public static void array_sequence() throws IOException {
		match("OBRACKET");
		expression();
		match("CBRACKET");
		if(array_sequence_pending()){
			array_sequence();
		}
	}
	public static boolean array_sequence_pending() throws IOException {
		return check("OBRACKET");
	}



/*Driver functions*/

	
	public static void recognize(String fileName) throws IOException {
		File f = new File(fileName);
		input = new PushbackInputStream(new FileInputStream(f));
		lx = new Lexer(input);
		advance();
		program();
	}
	public static void main(String[] args) throws IOException {
		recognize(args[0]);
	}
}