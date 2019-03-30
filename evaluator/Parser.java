import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

public class Parser {
	static PushbackInputStream input;
	static Lexeme current_lexeme;
	static Lexer lx;

/*Constructor*/
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
		return Lexeme.cons("PROGRAM",opt_import_statement_sequence(),class_definition());	
	}
	public static Lexeme opt_import_statement_sequence() throws IOException {
		if (import_statement_sequence_pending() == true){
			return import_statement_sequence();
		}
		return null;
	}
	public static Lexeme import_statement_sequence() throws IOException {
		Lexeme i = import_statement();
		Lexeme is = null;
		if (import_statement_sequence_pending()){
			is = import_statement_sequence();
		}
		return Lexeme.cons("IMPORT_STATEMENT_SEQUENCE", i, is);
	}
	public static boolean import_statement_sequence_pending() throws IOException {
		return import_statement_pending();
	}
	public static Lexeme import_statement() throws IOException {
		match("IMPORT");
		Lexeme i = identifier_sequence();
		match("SEMI");
		return Lexeme.cons("IMPORT_STATEMENT",i,null);
	}
	public static boolean import_statement_pending() throws IOException {
		return check("IMPORT");
	}
	public static Lexeme identifier_sequence() throws IOException {
		Lexeme a = match("ID");
		Lexeme b = null;
		if (check("DOT")){
			match("DOT");
			b = identifier_sequence();
			return Lexeme.cons("IDENTIFIER_SEQUENCE",a,b);
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
	public static boolean class_definition_pending() throws IOException {
		return check("CLASS");
	}
	public static Lexeme definition_sequence() throws IOException {
		Lexeme a = definition();
		Lexeme b = null;
		if (definition_sequence_pending()) {
			b = definition_sequence();
		}
		return Lexeme.cons("DEFINITION_SEQUENCE",a,b);
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
		else if (class_definition_pending()){
			return class_definition();
		}
		return null;
	}
	public static boolean definition_pending() throws IOException {
		return check("VAR") || check("FUNCTION");
	}
	public static Lexeme variable_def() throws IOException {
		match("VAR");
		Lexeme i = match("ID");
		Lexeme o = null;
		if (opt_init_pending()){
			o = opt_init();
		}
		match("SEMI");
		return Lexeme.cons("VARIABLE_DEF", i, o);
	}
	public static boolean variable_def_pending() throws IOException {
		return check("VAR");
	}
	public static Lexeme opt_init() throws IOException {
		return init();
	}
	public static boolean opt_init_pending() throws IOException {
		return init_pending();
	}
	public static Lexeme init() throws IOException {
		match("EQUALS");
		return expression();
	}
	public static boolean init_pending() throws IOException {
		return check("EQUALS");
	}
	public static Lexeme expression() throws IOException {
		Lexeme r = reg_expression();
		if (bool_operator_pending()){
			Lexeme b = bool_operator();
			Lexeme r2 = expression();
			Lexeme.setCar(b, r);
			Lexeme.setCdr(b, r2);
			return Lexeme.cons("EXPRESSION", b, null);
		}
		else if (operator_pending()){
			Lexeme o = operator();
			Lexeme r2 = expression();
			Lexeme.setCar(o, r);
			Lexeme.setCdr(o, r2);
			return Lexeme.cons("EXPRESSION", o, null);
		}
		return r;
	}
	public static boolean expression_pending() throws IOException {
		return reg_expression_pending();
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
		Lexeme b = null;
		if (check("COMMA")){
			match("COMMA");
			b = parameter_sequence();
		}
		return Lexeme.cons("PARAMETER_SEQUENCE",a,b);
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
		Lexeme b = null;
		if (statement_sequence_pending()){
			b = statement_sequence();
		}
		return Lexeme.cons("STATEMENT_SEQUENCE",a,b);
	}
	public static boolean statement_sequence_pending() throws IOException {
		return statement_pending();
	}
	public static Lexeme statement() throws IOException {
		if (id_statement_pending()){
			return id_statement();
		}
		else if (reg_expression_pending()){
			Lexeme r = reg_expression();
			match("SEMI");
			return r;
		}
		else if (variable_def_pending()){
			return variable_def();
		}
		else if ( method_def_pending() ){
			return method_def();
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
		if (reg_expression_pending()){
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
	public static Lexeme id_statement() throws IOException {
		Lexeme a = identifier_sequence();
		Lexeme b = null;
		if (init_pending()){
			b = init();
			match("SEMI");
			return Lexeme.cons("VAR_ASSIGN",a,b);
		}
		if (method_call_pending()){
			b = method_call();
			match("SEMI");
			return Lexeme.cons("METHOD_CALL",a,b);
		}
		return null;
	}
	public static boolean id_statement_pending() throws IOException {
		return identifier_sequence_pending();
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
		Lexeme e = bool_expression();
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
		Lexeme e = bool_expression();
		match("CPAREN");
		Lexeme b = block();
		return Lexeme.cons("WHILE_STATEMENT",e,b);
	}
	public static boolean while_statement_pending() throws IOException {
		return check("WHILE");
	}
	public static Lexeme reg_expression() throws IOException {
		Lexeme u = unary();
		if (operator_pending()){
			Lexeme o = operator();
			Lexeme e = reg_expression();
			Lexeme.setCar(o, u);
			Lexeme.setCdr(o, e);
			u = Lexeme.cons("REG_EXPRESSION",o,null);
		}
		return u;
	}
	public static boolean reg_expression_pending() throws IOException {
		return unary_pending();
	}
	public static Lexeme bool_expression() throws IOException {
		Lexeme r = reg_expression();
		if (bool_operator_pending()){
			Lexeme o = bool_operator();
			Lexeme e = reg_expression();
			Lexeme.setCar(o, r);
			Lexeme.setCdr(o, e);
			r = Lexeme.cons("BOOL_EXPRESSION",o,null);
		}
		return r;
	}
	public static boolean bool_expression_pending() throws IOException {
		return reg_expression_pending();
	}
	public static Lexeme operator() throws IOException {
		if (check("DIVIDE")){
			return match("DIVIDE");
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
	public static Lexeme bool_operator() throws IOException {
		if (check("EQUALS")){
			Lexeme e = match("EQUALS");
			match("EQUALS");
			return new Lexeme("IS_EQUAL", "==", e.lineNumber);
		}
		else if (check("EXCLAIM")){
			Lexeme e = match("EXCLAIM");
			if (optEqual_pending()){
				optEqual();
				return new Lexeme("NOT_EQUAL", "!=", e.lineNumber);
			}
			return new Lexeme("NOT", "!", e.lineNumber);
		}
		else if (check("FEWER")){
			Lexeme e = match("FEWER");
			if (optEqual_pending()){
				optEqual();
				return new Lexeme("FEWER_EQUAL", "<=", e.lineNumber);
			}
			return e;
		}
		else if (check("GREATER")){
			Lexeme e = match("GREATER");
			if (optEqual_pending()){
				optEqual();
				return new Lexeme("GREATER_EQUAL", ">=", e.lineNumber);
			}
			return e;
		}
		else if (check("VERT_BAR")){
			Lexeme e = match("VERT_BAR");
			match("VERT_BAR");
			return new Lexeme("OR", "||", e.lineNumber);
		}
		else if (check("AMPERSAND")){
			Lexeme e = match("AMPERSAND");
			match("AMPERSAND");
			return new Lexeme("AND", "&&", e.lineNumber);
		}
		return null;
	}
	public static boolean bool_operator_pending() throws IOException {
		if (check("EQUALS")){
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
		else if (check("VERT_BAR")){
			return true;
		}
		else if (check("AMPERSAND")){
			return true;
		}
		return false;
	}
	public static boolean optEqual_pending() throws IOException {
		if (check("EQUALS")){
			return true;
		}
		return false;
	}
	public static Lexeme optEqual() throws IOException {
		if (check("EQUALS")){
			return match("EQUALS");
		}
		return null;
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
		else if (check("NULL")){
			return match("NULL");
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
			if (opt_method_call_pending()){
				Lexeme o = opt_method_call();
				return Lexeme.cons("METHOD_CALL",i,o);
			}
			return i; //identifier sequence
		}
		else if (check("NEWLINE")){
			return match("NEWLINE");
		}
		else if (lamda_funct_pending()){
			return lamda_funct();
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
		else if (check("NULL")){
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
		else if (check("NEWLINE")){
			return true;
		}
		else if (lamda_funct_pending()){
			return true;
		}
		return false;
	}
	public static Lexeme lamda_funct() throws IOException {
		match("LAMBDA");
		match("OPAREN");
		Lexeme o = opt_parameter_sequence();
		match("CPAREN");
		Lexeme b = block();
		Lexeme glue = Lexeme.cons("GLUE",o,b);
		return Lexeme.cons("LAMBDA_FUNCTION",null,glue);
	}
	public static boolean lamda_funct_pending() throws IOException {
		return check("LAMBDA");
	}
	public static Lexeme opt_method_call() throws IOException {
		if (method_call_pending()){
			return method_call();
		}
		return null;
	}
	public static boolean opt_method_call_pending() throws IOException {
		return method_call_pending();
	}
	public static Lexeme method_call() throws IOException {
		match("OPAREN");
		Lexeme arg = opt_argument_sequence();
		match("CPAREN");
		return arg;
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
		Lexeme e = expression();
		Lexeme a = null;
		if (check("COMMA")) {
			match("COMMA");
			a = argument_sequence();	
		}
		return Lexeme.cons("ARGUMENT_SEQUENCE",e,a);
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
		Lexeme ptree = p.parse();
		System.out.println("legal");
		ptree.printLevelOrder(ptree);
	}
}