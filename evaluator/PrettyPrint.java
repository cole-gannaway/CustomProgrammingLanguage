import java.io.IOException;

public class PrettyPrint {

	public static void pp (Lexeme tree){
		if (tree == null) return;
		if (tree.type.equals("INTEGER")){ System.out.print(tree.value); }
		else if (tree.type.equals("REAL")){ System.out.print(Double.parseDouble(tree.value)); }
		else if (tree.type.equals("ID")){ System.out.print(tree.value); }
		else if (tree.type.equals("STRING")){ System.out.print('\"' + tree.value + '\"'); }
		else if (tree.type.equals("NULL")){ System.out.print(tree.value); }
		else if (tree.type.equals("DIVIDE")){ System.out.print(tree.value); }
		else if (tree.type.equals("EQUALS")){ System.out.print(tree.value); }
		else if (tree.type.equals("NEWLINE")){ System.out.print(tree.value); }
		else if (tree.type.equals("EXCLAIM")){ System.out.print(tree.value); }
		else if (tree.type.equals("FEWER")){ System.out.print(tree.value); }
		else if (tree.type.equals("GREATER")){ System.out.print(tree.value); }
		else if (tree.type.equals("MINUS")){ System.out.print(tree.value); }
		else if (tree.type.equals("PLUS")){ System.out.print(tree.value); }
		else if (tree.type.equals("TIMES")){ System.out.print(tree.value); }
		else if (tree.type.equals("BOOLEAN")){ System.out.print(tree.value); }
		else if (tree.type.equals("PROGRAM")){ pp(Lexeme.car(tree)); pp(Lexeme.cdr(tree)); }
		else if (tree.type.equals("IDENTIFIER_SEQUENCE")){ 
			pp(Lexeme.car(tree)); 
			System.out.print(".");
			pp(Lexeme.cdr(tree));
		}
		else if (tree.type.equals("CLASS_DEFINITION")){ 
			System.out.print("class ");
			pp(Lexeme.car(tree)); // id
			System.out.print("{ ");
			pp(Lexeme.cdr(tree));
			System.out.print("} ");
		}
		else if (tree.type.equals("VARIABLE_DEF")){ 
			System.out.print("var ");
			pp(Lexeme.car(tree));
			Lexeme r= Lexeme.cdr(tree);
			if (r!= null) { //optInit
				System.out.print(" = ");
				pp(r);
				System.out.print("; ");
			}
		}
		else if (tree.type.equals("METHOD_DEF")){ 
			System.out.print("function ");
			pp(Lexeme.car(tree)); //id
			Lexeme glue = Lexeme.cdr(tree);
			System.out.print("(");
			pp(Lexeme.car(glue));
			System.out.print(")");
			pp(Lexeme.cdr(glue));
		}
		else if (tree.type.equals("PARAMETER_SEQUENCE")){ 
			pp(Lexeme.car(tree)); 
			System.out.print(",");
			pp(Lexeme.cdr(tree));
		}
		else if (tree.type.equals("BLOCK")){ 
			System.out.print("{ ");
			pp(Lexeme.car(tree)); 
			System.out.print("} ");
		}
		else if (tree.type.equals("EXPRESSION")){ 
			pp(Lexeme.car(tree)); //unary
			System.out.print(" ");
			Lexeme glue = Lexeme.cdr(tree);
			pp(Lexeme.car(glue)); // operator sequence or operator
			System.out.print(" ");
			pp(Lexeme.cdr(glue));  // expression
		}
		else if (tree.type.equals("OPERATOR_SEQUENCE")){ 
			pp(Lexeme.car(tree));
			pp(Lexeme.cdr(tree));
		}
		else if (tree.type.equals("STATEMENT")){
			pp(Lexeme.car(tree));
			System.out.print("; ");
		}
		else if (tree.type.equals("RETURN_STATEMENT")){
			System.out.print("return ");
			pp(Lexeme.car(tree));
			System.out.print("; ");
		}
		else if (tree.type.equals("IF_STATEMENT")){
			System.out.print("if ");
			System.out.print("(");
			Lexeme e = Lexeme.car(tree);
			pp(e); //expression
			System.out.print(")");
			Lexeme glue = Lexeme.cdr(tree);
			pp(glue); //glue
		}
		else if (tree.type.equals("ELSE_STATEMENT")){
			System.out.print("else ");
			pp(Lexeme.car(tree));
		}
		else if (tree.type.equals("WHILE_STATEMENT")){
			System.out.print("while ");
			System.out.print("(");
			pp(Lexeme.car(tree)); //expression
			System.out.print(")");
			pp(Lexeme.cdr(tree)); //block
		}
		else if (tree.type.equals("PAREN_EXPRESSION")){
			System.out.print("(");
			pp(Lexeme.car(tree)); //expression
			System.out.print(")");
		}
		else if (tree.type.equals("GLUE")){
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