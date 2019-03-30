import java.util.Queue; 
import java.util.LinkedList;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Lexeme {
	String type;
	String value;
	Integer lineNumber;
	Lexeme left;
	Lexeme right;
	Scanner sc;
	Lexeme arr[];

	Lexeme (String t, String v, Integer ln){
		type = t;
		value = v;
		lineNumber = ln;
		left = null;
		right = null;
		sc = null;
		arr = null;
	}
/*Lexeme functions */
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
	public static Lexeme caddr(Lexeme top_lexeme){
		return car(cdr(cdr(top_lexeme)));
	}
/*Helper Funcitons */
	public static boolean isSameValue(Lexeme l1, Lexeme l2){
		return (l1.value.equals(l2.value));
	}
	public Double getValDouble(){
		if (value != null) return Double.parseDouble(value);
		return null;
	}
	public Integer getValInteger(){
		if (value != null) return Integer.parseInt(value);
		return null;
	}
/*Reserved Functions */
	public void setFilePtr(String fileName) throws IOException {
		sc = new Scanner( new File(fileName));
	}
	public Lexeme readIntFilePtr() throws IOException {
		Integer num = sc.nextInt();
		return new Lexeme("INTEGER", num.toString(), 0);
	}
	public boolean isEndOfFile() throws IOException {
		return (!sc.hasNextInt());
	}
	public void closeFile() throws IOException {
		sc.close();
	}
/*Display Functions */
	public void displayType(){
		if (type == null){
			System.out.print("NULL");
		}
		else{
			System.out.print(type);
		}
	}
	public void displayValue(){
		if (type == "STRING"){
			System.out.print("\"" + value + "\"");
		}
		else{
			System.out.print(value);
		}
	}
	public static void printLevelOrder(Lexeme root) { 
        Queue<Lexeme> queue = new LinkedList<Lexeme>(); 
        queue.add(root); 
        while (!queue.isEmpty())  
        { 
  
            /* poll() removes the present head. 
            For more information on poll() visit  
            http://www.tutorialspoint.com/java/util/linkedlist_poll.htm */
            Lexeme tempLexeme = queue.poll(); 
            tempLexeme.debug(); System.out.print(" "); 
  
            /*Enqueue left child */
            if (tempLexeme.left != null) { 
                queue.add(tempLexeme.left); 
            } 
  
            /*Enqueue right child */
            if (tempLexeme.right != null) { 
                queue.add(tempLexeme.right); 
            } 
        } 
    } 
	public static void preorder (Lexeme root){
		if (root == null) return;
		root.debugln();
		preorder(root.left);
		preorder(root.right);
		
	}
	public void debug(){
		if (type != null) System.out.print(type + " ");
		if (value != null) System.out.print(value + " ");
		System.out.print(lineNumber);
	}
	public void debugln(){
		this.debug();
		System.out.println();
	}
}
