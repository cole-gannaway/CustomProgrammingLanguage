
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
	public void displayValue(){
		if (type == "STRING"){
			System.out.print("\"" + value + "\"");
		}
		else{
			System.out.print(value);
		}
	}
	public static Lexeme cons(String top_type, Lexeme left_lexeme, Lexeme right_lexeme) { //newLexeme with type, left, and right as arguments
		Lexeme top_lexeme = new Lexeme(top_type,null,0);
		top_lexeme.left = left_lexeme;
		top_lexeme.right = right_lexeme;
		return top_lexeme;
	}
	public static Lexeme car(Lexeme top_lexeme){  // getLexemeLeft
		return top_lexeme.left;
	}
	public static void setCar(Lexeme top_lexeme, Lexeme left_lexeme){ //setLexemeLeft
		top_lexeme.left = left_lexeme;
	}
	public static Lexeme cdr(Lexeme top_lexeme){ // getLexemeRight
		return top_lexeme.right;
	}
	public static void setCdr(Lexeme top_lexeme, Lexeme right_lexeme){ //setLexemeRight
		top_lexeme.right = right_lexeme;
	}
	public static Lexeme cadr(Lexeme top_lexeme){
		return car(cdr(top_lexeme));
	}
	public Lexeme copy(){
		Lexeme copy_lexeme = new Lexeme(type,value,lineNumber);
		copy_lexeme.left = left;
		copy_lexeme.right = right;
		return copy_lexeme;
	}
	public static boolean isSameValue(Lexeme l1, Lexeme l2){
		return (l1.value == l2.value);
	}
/*Template
	public static Lexeme nameOffunction(){

	}
*/

}
