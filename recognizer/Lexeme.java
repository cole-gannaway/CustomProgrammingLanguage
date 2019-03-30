
public class Lexeme {
	String type;
	String value;
	Integer lineNumber;
	Lexeme left;
	Lexeme right;
	
	Lexeme (String t, String v, Integer ln){
		type = t;
		value = v;
		lineNumber = ln;
		left = null;
		right = null;
	}

	public static Lexeme cons(String top_type, Lexeme left_lexeme, Lexeme right_lexeme) {
		Lexeme top_lexeme = new Lexeme(top_type,null,0);
		top_lexeme.left = left_lexeme;
		top_lexeme.right = right_lexeme;
		return top_lexeme;
	}
	public static Lexeme car(Lexeme top_lexeme){
		return top_lexeme.left;
	}
	public static void setCar(Lexeme top_lexeme, Lexeme left_lexeme){
		top_lexeme.left = left_lexeme;
	}
	public static Lexeme cdr(Lexeme top_lexeme){
		return top_lexeme.right;
	}
	public static void setCdr(Lexeme top_lexeme, Lexeme right_lexeme){
		top_lexeme.right = right_lexeme;
	}
	
/*Template
	public static Lexeme nameOffunction(){

	}
*/

}
