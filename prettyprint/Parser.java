import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

public class Parser {
	static PushbackInputStream input;
	static Lexeme current_lexeme;
	static Lexer lx;
	
	Parser (String fileName) throws IOException {
		File f = new File(fileName);
		input = new PushbackInputStream(new FileInputStream(f));
		lx = new Lexer(input);
	}

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
			matchError(type);	
		}
		catch (IOException e){
			System.out.println("illegal");
			System.exit(-1);
   		}
   		return null;
	}
	public static void matchError(String type) throws IOException {
		System.out.printf("\nLexeme errored on line #%d\t%-20s\t%-20s\n",current_lexeme.lineNumber,current_lexeme.type,current_lexeme.value);
		System.out.printf("\033[0;31m"); //Set the text to the color red
		System.out.printf("\033[31m----Syntax Error: on line #%d\t%-20s\t%-20s\t%-20s\n",current_lexeme.lineNumber,("Expected: "+ type),("Got type:" + current_lexeme.type),current_lexeme.value);
		System.out.printf("\033[0m"); //Resets the text to default color
		throw(new IOException("illegal"));
	}
	



/*Grammar functions*/

	public static Lexeme program() throws IOException {
		return Lexeme.cons("PROGRAM",opt_aux_file(),class_definition());
		
	}
	public static Lexeme opt_aux_file() throws IOException {
		Lexeme tree = null;
		if (aux_file_pending() == true){
			tree = aux_file();
			match("SEMI");
		}
		return tree;
	}
	public static Lexeme aux_file() throws IOException {
		match("IMPORT");
		return Lexeme.cons("AUX_FILE",identifier_sequence(),null);
	}
	public static boolean aux_file_pending() throws IOException {
		return check("IMPORT");
	}
	public static Lexeme identifier_sequence() throws IOException {
		Lexeme a = match("ID");
		if (check("DOT")){
			match("DOT");
			Lexeme b = identifier_sequence();
			a = Lexeme.cons("IDENTIFIER_SEQUENCE",a,b);
		}
		return a;
	}
	public static boolean identifier_sequence_pending() throws IOException {
		return check("ID");
	}	
	public static Lexeme class_definition() throws IOException {
		match("CLASS");
		Lexeme a = match("ID");
		match("OBRACE");
		Lexeme b = definition_sequence();
		match("CBRACE");
		return Lexeme.cons("CLASS_DEFINITION",a,b);
	}
	public static Lexeme definition_sequence() throws IOException {
		Lexeme a = definition();
		if (definition_sequence_pending()) {
			Lexeme b = definition_sequence();
			a = Lexeme.cons("DEFINITION_SEQUENCE",a,b);
		}
		return a;
	}
	public static boolean definition_sequence_pending() throws IOException {
		return definition_pending();
	}
	public static Lexeme definition() throws IOException {
		if (variable_def_pending()){
			return variable_def();
		}
		else if (method_def_pending()){
			return method_def();
		}
		return null;
	}
	public static boolean definition_pending() throws IOException {
		return check("VAR") || check("FUNCTION");
	}
	public static Lexeme variable_def() throws IOException {
		match("VAR");
		Lexeme i = match("ID");
		if (optInit_pending()){
			Lexeme o = optInit();
			i = Lexeme.cons("VARIABLE_DEF", i, o);
		}
		match("SEMI");
		return i;
	}
	public static boolean variable_def_pending() throws IOException {
		return check("VAR");
	}
	public static Lexeme optInit() throws IOException {
		match("EQUALS");
		return expression();
	}
	public static boolean optInit_pending() throws IOException {
		return check("EQUALS");
	}
	public static Lexeme method_def() throws IOException {
		match("FUNCTION");
		Lexeme i = match("ID");
		match("OPAREN");
		Lexeme o = opt_parameter_sequence(); 
		match("CPAREN");
		Lexeme b = block();
		o = Lexeme.cons("GLUE",o,b);
		return Lexeme.cons("METHOD_DEF",i,o);
	}
	public static boolean method_def_pending() throws IOException {
		return check("FUNCTION");
	}
	public static Lexeme opt_parameter_sequence() throws IOException {
		if (parameter_sequence_pending()){
			return parameter_sequence();
		}
		return null;
	}
	public static Lexeme parameter_sequence() throws IOException {
		Lexeme a = unary();
		if (check("COMMA")){
			match("COMMA");
			Lexeme b = parameter_sequence();
			a = Lexeme.cons("PARAMETER_SEQUENCE",a,b);
		}
		return a;
	}
	public static boolean parameter_sequence_pending() throws IOException {
		return unary_pending();
	}
	public static Lexeme block() throws IOException {
		match("OBRACE");
		Lexeme s = statement_sequence();
		match("CBRACE");
		return Lexeme.cons("BLOCK",s,null);
	}
	public static boolean block_pending() throws IOException {
		return check("OBRACE");
	}
	public static Lexeme statement_sequence() throws IOException {
		Lexeme a = statement();
		if (statement_sequence_pending()){
			Lexeme b = statement_sequence();
			a = Lexeme.cons("STATEMENT_SEQUENCE",a,b);
		}
		return a;
	}
	public static boolean statement_sequence_pending() throws IOException {
		return statement_pending();
	}
	public static Lexeme statement() throws IOException {
		if (expression_pending()){
			Lexeme e = expression();
			match("SEMI");
			return Lexeme.cons("STATEMENT",e,null);
		}
		else if (variable_def_pending()){
			return variable_def();
		}
		else if (return_statement_pending()){
			return return_statement();	
		}
		else if (if_statement_pending()){
			return if_statement();
		}
		else if (while_statement_pending()){
			return while_statement();
		}
		return null;
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
	public static Lexeme return_statement() throws IOException {
		match("RETURN");
		Lexeme a = expression();
		match("SEMI");
		return Lexeme.cons("RETURN_STATEMENT",a,null);
	}
	public static boolean return_statement_pending() throws IOException {
		return check("RETURN");
	}
	public static Lexeme if_statement() throws IOException {
		match("IF");
		match("OPAREN");
		Lexeme e = expression();
		match("CPAREN");
		Lexeme b = block();
		Lexeme oe = optElse();
		Lexeme glue = Lexeme.cons("GLUE",b,oe);
		Lexeme tree = Lexeme.cons("IF_STATEMENT",e,glue);
		return tree;
	}
	public static boolean if_statement_pending() throws IOException {
		return check("IF");
	}
	public static Lexeme optElse() throws IOException {
		if (check("ELSE")){
			match("ELSE");
			return else_statement();
		}
		return null;
	}
	public static boolean optElse_pending() throws IOException {
		return check("ELSE");
	}
	public static Lexeme else_statement() throws IOException {
		if (if_statement_pending()) {
			return Lexeme.cons("ELSE_STATEMENT",if_statement(),null);
		}
		else if (block_pending()){
			return Lexeme.cons("ELSE_STATEMENT",block(),null);
		}
		return null;
	}
	public static Lexeme while_statement() throws IOException {
		match("WHILE");
		match("OPAREN");
		Lexeme e = expression();
		match("CPAREN");
		Lexeme b = block();
		return Lexeme.cons("WHILE_STATEMENT",e,b);
	}
	public static boolean while_statement_pending() throws IOException {
		return check("WHILE");
	}
	public static Lexeme expression() throws IOException {
		Lexeme u = unary();
		if (operator_sequence_pending()){
			Lexeme o = operator_sequence();
			Lexeme e = expression();
			Lexeme glue = Lexeme.cons("GLUE",o,e);
			u = Lexeme.cons("EXPRESSION",u,glue);
		}
		return u;
	}
	public static boolean expression_pending() throws IOException {
		return unary_pending();
	}
	public static Lexeme operator_sequence() throws IOException {
		Lexeme o = operator();
		if (operator_sequence_pending()){
			Lexeme s = operator_sequence();
			o = Lexeme.cons("OPERATOR_SEQUENCE",o,s);
		}
		return o;
	}
	public static boolean operator_sequence_pending() throws IOException {
		return operator_pending();
	}
	public static Lexeme operator() throws IOException {
		if (check("DIVIDE")){
			return match("DIVIDE");
		}
		else if (check("EQUALS")){
			return match("EQUALS");
		}
		else if (check("EXCLAIM")){
			return match("EXCLAIM");
		}
		else if (check("FEWER")){
			return match("FEWER");
		}
		else if (check("GREATER")){
			return match("GREATER");
		}
		else if (check("MINUS")){
			return match("MINUS");
		}
		else if (check("PLUS")){
			return match("PLUS");
		}
		else if (check("TIMES")){
			return match("TIMES");
		}
		return null;
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
	public static Lexeme unary() throws IOException {
		if (check("INTEGER")){
			return match("INTEGER");
		}
		else if (check("REAL")){
			return match("REAL");
		}
		else if (check("BOOLEAN")){
			return match("BOOLEAN");
		}
		else if (check("CHARACTER")){
			return match("CHARACTER");
		}
		else if (check("STRING")){
			return match("STRING");
		}
		else if (check("OPAREN")){
			match("OPAREN");
			Lexeme e = expression();
			match("CPAREN");
			return Lexeme.cons("PAREN_EXPRESSION", e, null); // remember to put parenthesis around
		}
		else if (check("MINUS")){
			match("MINUS");
			Lexeme u = unary();
			return Lexeme.cons("UMINUS",u,null);
		}
		else if (identifier_sequence_pending()){
			Lexeme i = identifier_sequence();
			Lexeme o = opt_method_call();
			return Lexeme.cons("UNARY",i,o);
		}
		return null;
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
	public static Lexeme opt_method_call() throws IOException {
		if (method_call_pending()){
			return method_call();
		}
		return null;
	}
	public static Lexeme method_call() throws IOException {
		match("OPAREN");
		Lexeme arg = opt_argument_sequence();
		match("CPAREN");
		return Lexeme.cons("METHOD_CALL",arg,null);
	}
	public static boolean method_call_pending() throws IOException {
		return check("OPAREN");
	}
	public static Lexeme opt_argument_sequence() throws IOException {
		if (argument_sequence_pending()){
			return argument_sequence();
		}
		return null;
	}
	public static Lexeme argument_sequence() throws IOException {
		Lexeme tree = new Lexeme("ARGUMENT_SEQUENCE",null,0);
		Lexeme e = expression();
		if (check("COMMA")) {
			match("COMMA");
			Lexeme a = argument_sequence();
			e = Lexeme.cons("ARGUMENT_SEQUENCE",e,a);
		}
		return e;
	}
	public static boolean argument_sequence_pending() throws IOException {
		return expression_pending();
	}
	
/*Template
	public static Lexeme ________________() throws IOException {
	}
	public static boolean ________________() throws IOException {
	}
*/



/*Driver functions*/
	
	public static Lexeme parse () throws IOException {		
		advance();
		Lexeme p = program();
		return p;
	}
	public static void main(String[] args) throws IOException {
		Parser p = new Parser(args[0]);
		p.parse();
		System.out.println("legal");
	}
}