import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

public class PrettyPrint {

	public static void pp (Lexeme tree){
		if (tree == null) return;
		if (tree.type == "INTEGER"){ System.out.print(tree.value); }
		else if (tree.type == "REAL"){ System.out.print(Double.parseDouble(tree.value)); }
		else if (tree.type == "ID"){ System.out.print(tree.value); }
		else if (tree.type == "STRING"){ System.out.print('\"' + tree.value + '\"'); }
		else if (tree.type == "REAL"){ System.out.print(tree.value); }
		else if (tree.type == "DIVIDE"){ System.out.print(tree.value); }
		else if (tree.type == "EQUALS"){ System.out.print(tree.value); }
		else if (tree.type == "EXCLAIM"){ System.out.print(tree.value); }
		else if (tree.type == "FEWER"){ System.out.print(tree.value); }
		else if (tree.type == "GREATER"){ System.out.print(tree.value); }
		else if (tree.type == "MINUS"){ System.out.print(tree.value); }
		else if (tree.type == "PLUS"){ System.out.print(tree.value); }
		else if (tree.type == "TIMES"){ System.out.print(tree.value); }
		else if (tree.type == "BOOLEAN"){ System.out.print(tree.value); }
		else if (tree.type == "PROGRAM"){ pp(Lexeme.car(tree)); pp(Lexeme.cdr(tree)); }
		else if (tree.type == "AUX_FILE"){ 
			System.out.print("import ");
			pp(Lexeme.car(tree));
			System.out.print("; ");
		}
		else if (tree.type == "IDENTIFIER_SEQUENCE"){ 
			pp(Lexeme.car(tree)); 
			System.out.print(".");
			pp(Lexeme.cdr(tree));
		}
		else if (tree.type == "CLASS_DEFINITION"){ 
			System.out.print("class ");
			pp(Lexeme.car(tree)); // id
			System.out.print("{ ");
			pp(Lexeme.cdr(tree));
			System.out.print("} ");
		}
		else if (tree.type == "VARIABLE_DEF"){ 
			System.out.print("var ");
			pp(Lexeme.car(tree));
			Lexeme r= Lexeme.cdr(tree);
			if (r!= null) { //optInit
				System.out.print(" = ");
				pp(r);
				System.out.print("; ");
			}
		}
		else if (tree.type == "METHOD_DEF"){ 
			System.out.print("function ");
			pp(Lexeme.car(tree)); //id
			Lexeme glue = Lexeme.cdr(tree);
			System.out.print("(");
			pp(Lexeme.car(glue));
			System.out.print(")");
			pp(Lexeme.cdr(glue));
		}
		else if (tree.type == "PARAMETER_SEQUENCE"){ 
			pp(Lexeme.car(tree)); 
			System.out.print(",");
			pp(Lexeme.cdr(tree));
		}
		else if (tree.type == "BLOCK"){ 
			System.out.print("{ ");
			pp(Lexeme.car(tree)); 
			System.out.print("} ");
		}
		else if (tree.type == "EXPRESSION"){ 
			pp(Lexeme.car(tree)); //unary
			System.out.print(" ");
			Lexeme glue = Lexeme.cdr(tree);
			pp(Lexeme.car(glue)); // operator sequence or operator
			System.out.print(" ");
			pp(Lexeme.cdr(glue));  // expression
		}
		else if (tree.type == "OPERATOR_SEQUENCE"){ 
			pp(Lexeme.car(tree));
			pp(Lexeme.cdr(tree));
		}
		else if (tree.type == "STATEMENT"){
			pp(Lexeme.car(tree));
			System.out.print("; ");
		}
		else if (tree.type == "RETURN_STATEMENT"){
			System.out.print("return ");
			pp(Lexeme.car(tree));
			System.out.print("; ");
		}
		else if (tree.type == "IF_STATEMENT"){
			System.out.print("if ");
			System.out.print("(");
			Lexeme e = Lexeme.car(tree);
			pp(e); //expression
			System.out.print(")");
			Lexeme glue = Lexeme.cdr(tree);
			pp(glue); //glue
		}
		else if (tree.type == "ELSE_STATEMENT"){
			System.out.print("else ");
			pp(Lexeme.car(tree));
		}
		else if (tree.type == "WHILE_STATEMENT"){
			System.out.print("while ");
			System.out.print("(");
			pp(Lexeme.car(tree)); //expression
			System.out.print(")");
			pp(Lexeme.cdr(tree)); //block
		}
		else if (tree.type == "PAREN_EXPRESSION"){
			System.out.print("(");
			pp(Lexeme.car(tree)); //expression
			System.out.print(")");
		}
		else if (tree.type == "GLUE"){
			pp(Lexeme.car(tree));
			pp(Lexeme.cdr(tree));
		}
		else{ 
			pp(Lexeme.car(tree));
			pp(Lexeme.cdr(tree));
		}
		
	}

	public static void main(String[] args) throws IOException {
		Parser p = new Parser(args[0]);
		Lexeme ptree = p.parse();
		pp(ptree);
		System.out.print("\n");
	}


}