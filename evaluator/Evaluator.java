import java.io.IOException;

public class Evaluator {
    public static Integer countCL;
    public static String[] argsCL;

/*Recursive function */
    public static Lexeme eval(Lexeme tree, Lexeme env) throws IOException {
    /*Self evaluating */
        if (tree == null) { /* System.out.println("FOUND NULL"); */ return tree; } 
        //System.out.print("evaluating ");tree.debug();
        if (tree.type.equals("INTEGER")) { return tree; }
        else if (tree.type.equals("REAL")){ return tree; }
        else if (tree.type.equals("STRING")) { return tree; }
        else if (tree.type.equals("BOOLEAN")) { return tree; }
        else if (tree.type.equals("NULL")){ return tree; }
        else if (tree.type.equals("EXCLAIM")) { return tree; }
        else if (tree.type.equals("NEWLINE")) { return tree; }
        else if (tree.type.equals("EQUALS")) { return tree; }
    /*Dot operator evals lhs, rhs a variable */
        else if (tree.type.equals("DOT")) { return evalDot(tree,env); }
    /*Find value of variables in the environment */
        else if (tree.type.equals("ID")) { return Environment.lookup(tree, env); }
    /*Slightly more complex */
        else if (tree.type.equals("UMINUS")){ return evalUMinus(tree,env); }
        else if (tree.type.equals("RETURNED")){ return evalReturned(tree,env); }
    /*Operators (both sides evaluated) */
        else if (tree.type.equals("PLUS")) { return evalSimpleOp(tree,env); }
        else if (tree.type.equals("TIMES")) { return evalSimpleOp(tree,env); }
        else if (tree.type.equals("MINUS")) { return evalSimpleOp(tree,env); }
        else if (tree.type.equals("DIVIDE")) { return evalSimpleOp(tree,env); }
    /*Boolean_operators (both sides evaluated) */
        else if (tree.type.equals("IS_EQUAL")) { return evalBoolOp(tree,env); }
        else if (tree.type.equals("NOT_EQUAL")) { return evalBoolOp(tree,env); }
        else if (tree.type.equals("FEWER")) { return evalBoolOp(tree,env); }
        else if (tree.type.equals("FEWER_EQUAL")) { return evalBoolOp(tree,env); }
        else if (tree.type.equals("GREATER")) { return evalBoolOp(tree,env); }
        else if (tree.type.equals("GREATER_EQUAL")) { return evalBoolOp(tree,env); }
        else if (tree.type.equals("NOT")) { return evalBoolOp(tree,env); } //FIXME
    /*AND and OR short-circuit */
        else if (tree.type.equals("OR")) { return evalBoolOp(tree,env); }
        else if (tree.type.equals("AND")) { return evalBoolOp(tree,env); }
    /*Expressions */
        else if (tree.type.equals("PAREN_EXPRESSION")){ return eval(Lexeme.car(tree),env); }
        else if (tree.type.equals("EXPRESSION")){ return eval(Lexeme.car(tree),env); }
        else if (tree.type.equals("REG_EXPRESSION")) { return eval(Lexeme.car(tree), env); }
        else if (tree.type.equals("BOOL_EXPRESSION")) { return eval(Lexeme.car(tree), env); }
    /*Statements */
        else if (tree.type.equals("VAR_ASSIGN")) { return evalVarAssign(tree,env); }
        else if (tree.type.equals("IF_STATEMENT")) return evalIf(tree,env);
        else if (tree.type.equals("ELSE_STATEMENT")) return evalElse(tree,env);
        else if (tree.type.equals("RETURN_STATEMENT")) return evalReturn(tree,env);
        else if (tree.type.equals("WHILE_STATEMENT")) return evalWhile(tree,env);
        else if (tree.type.equals("IMPORT_STATEMENT")) { return evalImportStatementSeq(tree,env); }
    /*Sequences */
        else if (tree.type.equals("IDENTIFIER_SEQUENCE")) { return evalIdSeq(tree,env); }
        else if (tree.type.equals("PARAMETER_SEQUENCE")) {  return evalParamSeq(tree,env); }
        else if (tree.type.equals("STATEMENT_SEQUENCE")) { return evalStatementSeq(tree,env); }
        else if (tree.type.equals("IMPORT_STATEMENT_SEQUENCE")) { return evalImportStatementSeq(tree,env); }
        else if (tree.type.equals("DEFINITION_SEQUENCE")) { return evalDefSeq(tree,env); }
        else if (tree.type.equals("ARGUMENT_SEQUENCE")) { return evalArgsSeq(tree,env); };
    /*Definitions */
        if (tree.type.equals("VARIABLE_DEF")) return evalVariableDef(tree,env);
        else if (tree.type.equals("METHOD_DEF")) return evalMethodDef(tree,env);
        else if (tree.type.equals("LAMBDA_FUNCTION")) return evalLambda(tree,env);
        else if (tree.type.equals("CLASS_DEFINITION")) { return evalClassDef(tree,env); }    
    /*Function calls */
        else if (tree.type.equals("METHOD_CALL")) { return evalMethodCall(tree,env); }
    /*Program and function body are parsed as blocks */
        else if (tree.type.equals("BLOCK")) {return evalBlock(tree,env);}
        else if (tree.type.equals("PROGRAM")) { return evalProgram(tree,env); }
    /*Else*/
        else {
            System.out.println("Found " + tree.type); 
            System.exit(-1); 
            return null;
        }
    }
/*Helper functions */
/*Slightly more complex */
    public static Lexeme evalUMinus(Lexeme tree, Lexeme env) throws IOException {
        Lexeme num = eval(Lexeme.car(tree),env);
        if (num.type.equals("INTEGER")){
            num.value = "-".concat(num.value);
            return num;
        }
        else if (num.type.equals("REAL")){
            num.value = "-".concat(num.value);
            return num;
        }
        else {
            System.out.println("Found " + tree.type); 

        }
        return null;
    }
    public static Lexeme evalReturned(Lexeme tree, Lexeme env) throws IOException {
        Lexeme last = eval(Lexeme.car(tree),env);
        return last;
    }

    
/*expressions */
/*Operators */
    public static Lexeme evalDot(Lexeme tree, Lexeme env) throws IOException {
        Lexeme object = eval(getLHS(tree), env);
        return eval(getRHS(tree), object); // objects == environments!
    }
    public static Lexeme evalSimpleOp(Lexeme tree, Lexeme env) throws IOException {
        if (tree.type.equals("PLUS")) return evalPlus(tree,env);
        else if (tree.type.equals("MINUS")) return evalMinus(tree,env);
        else if (tree.type.equals("TIMES")) return evalTimes(tree,env);
        else if (tree.type.equals("DIVIDE")) return evalDivide(tree,env);
        return null;
    }
    
    public static Lexeme evalPlus(Lexeme tree, Lexeme env) throws IOException {
        //eval the left and the right hand sides
        Lexeme left = eval(tree.left,env);
        Lexeme right = eval(tree.right,env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER")){
            Integer sum = (left.getValInteger() + right.getValInteger());
            return new Lexeme("INTEGER", sum.toString() ,left.lineNumber);
        }
        else if (left.type.equals("REAL") && right.type.equals("REAL")){
            Double sum = (left.getValDouble() + right.getValDouble() );
            return new Lexeme("REAL", sum.toString() ,left.lineNumber);
        }
        else if (left.type.equals("INTEGER") && right.type.equals("REAL")){
            Double sum = ((double)left.getValInteger() + right.getValDouble() );
            return new Lexeme("REAL", sum.toString() ,left.lineNumber);
        }
        else if (left.type.equals("REAL") && right.type.equals("INTEGER")){
            Double sum = (left.getValDouble() + (double)right.getValInteger());
            return new Lexeme("REAL", sum.toString() ,left.lineNumber);
        }
        else if (left.type.equals("STRING") && right.type.equals("STRING")){
            return new Lexeme("STRING", left.value.concat(right.value),left.lineNumber);
        }
        return tree;
    }
    public static Lexeme evalMinus(Lexeme tree, Lexeme env) throws IOException {
        //eval the left and the right hand sides
        Lexeme left = eval(tree.left,env);
        Lexeme right = eval(tree.right,env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER")){
            Integer diff = (left.getValInteger() - right.getValInteger());
            return new Lexeme("INTEGER", diff.toString() ,left.lineNumber);
        }
        else if (left.type.equals("REAL") && right.type.equals("REAL")){
            Double diff = (left.getValDouble() - right.getValDouble() );
            return new Lexeme("REAL", diff.toString() ,left.lineNumber);
        }
        else if (left.type.equals("INTEGER") && right.type.equals("REAL")){
            Double diff = ((double)left.getValInteger() - right.getValDouble() );
            return new Lexeme("REAL", diff.toString() ,left.lineNumber);
        }
        else if (left.type.equals("REAL") && right.type.equals("INTEGER")){
            Double diff = (left.getValDouble() - (double)right.getValInteger());
            return new Lexeme("REAL", diff.toString() ,left.lineNumber);
        }
        else if (left.type.equals("STRING") && right.type.equals("STRING")){
            try{
                semanticError(left, "Can not subtract two strings!");
            }
            catch (IOException e){
                System.out.println("");
                System.exit(-1);
            }
        }
        return tree;
    }
    public static Lexeme evalTimes(Lexeme tree, Lexeme env) throws IOException {
        //eval the left and the right hand sides
        Lexeme left = eval(tree.left,env);
        Lexeme right = eval(tree.right,env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER")){
            Integer product = (left.getValInteger() * right.getValInteger());
            return new Lexeme("INTEGER", product.toString() ,left.lineNumber);
        }
        else if (left.type.equals("REAL") && right.type.equals("REAL")){
            Double product = (left.getValDouble() * right.getValDouble() );
            return new Lexeme("REAL", product.toString() ,left.lineNumber);
        }
        else if (left.type.equals("INTEGER") && right.type.equals("REAL")){
            Double product = ((double)left.getValInteger() * right.getValDouble() );
            return new Lexeme("REAL", product.toString() ,left.lineNumber);
        }
        else if (left.type.equals("REAL") && right.type.equals("INTEGER")){
            Double product = (left.getValDouble() * (double)right.getValInteger());
            return new Lexeme("REAL", product.toString() ,left.lineNumber);
        }
        else if (left.value.equals("null") || right.value.equals("null")){
            try{
                semanticError(left, "Can not multiply by null!");
            }
            catch (IOException e){
                System.out.println("");
                System.exit(-1);
            }
        }
        return tree;
    }
    public static Lexeme evalDivide(Lexeme tree, Lexeme env) throws IOException {
        //eval the left and the right hand sides
        Lexeme left = eval(tree.left,env);
        Lexeme right = eval(tree.right,env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER")){
            Integer dividend = (left.getValInteger() / right.getValInteger());
            return new Lexeme("INTEGER", dividend.toString() ,left.lineNumber);
        }
        else if (left.type.equals("REAL") && right.type.equals("REAL")){
            Double dividend = (left.getValDouble() / right.getValDouble() );
            return new Lexeme("REAL", dividend.toString() ,left.lineNumber);
        }
        else if (left.type.equals("INTEGER") && right.type.equals("REAL")){
            Double dividend = ((double)left.getValInteger() / right.getValDouble() );
            return new Lexeme("REAL", dividend.toString() ,left.lineNumber);
        }
        else if (left.type.equals("REAL") && right.type.equals("INTEGER")){
            Double dividend = (left.getValDouble() / (double)right.getValInteger());
            return new Lexeme("REAL", dividend.toString() ,left.lineNumber);
        }
        return tree;
    }
/*Boolean Operators */    
    public static Lexeme evalBoolOp(Lexeme tree, Lexeme env) throws IOException {
        if (tree.type.equals("IS_EQUAL")) { return evalIsEqual(tree,env); }
        else if (tree.type.equals("NOT_EQUAL")) { return evalNotEqual(tree,env); }
        else if (tree.type.equals("FEWER")) { return evalFewer(tree,env); }
        else if (tree.type.equals("FEWER_EQUAL")) { return evalFewerEqual(tree,env); }
        else if (tree.type.equals("GREATER")) { return evalGreater(tree,env); }
        else if (tree.type.equals("GREATER_EQUAL")) { return evalGreaterEqual(tree,env); }
        else if (tree.type.equals("OR")) { return evalOr(tree,env); }
        else if (tree.type.equals("AND")) { return evalAnd(tree,env); }
        return null;
    }
    public static Lexeme evalIsEqual(Lexeme tree, Lexeme env) throws IOException {
        //eval the left and the right hand sides
        Lexeme left = eval(tree.left,env);
        Lexeme right = eval(tree.right,env);
        if (left.type.equals("ENV")){ // comparing an object
            Lexeme table = Lexeme.car(tree);
            Lexeme ids = Lexeme.car(table);
            if (table == null) { return new Lexeme("BOOLEAN", "true",left.lineNumber);}
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left != null && left.value.equals("null")){
            if (right.value.equals("null")) { return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (right != null && right.value.equals("null")){
            if (left.value.equals("null")) { return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER")){
            if (left.getValInteger() == right.getValInteger()){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left.type.equals("REAL") && right.type.equals("REAL")){
            if (left.getValDouble() == right.getValDouble()){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left.type.equals("BOOLEAN") && right.type.equals("BOOLEAN")){
            if (left.value.equals(right.value)){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left.type.equals("INTEGER") && right.type.equals("REAL")){
            if ((double)left.getValInteger() == right.getValDouble()){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left.type.equals("REAL") && right.type.equals("INTEGER")){
            if (left.getValDouble() == (double)right.getValInteger()){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left.type.equals("STRING") && right.type.equals("STRING")){
            if (left.value.equals(right.value)){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        return tree;
    }
    public static Lexeme evalNotEqual(Lexeme tree, Lexeme env) throws IOException {
        //eval the left and the right hand sides
        Lexeme left = eval(tree.left,env);
        Lexeme right = eval(tree.right,env);
        if (left.type.equals("ENV")){ // comparing an object
            Lexeme table = Lexeme.car(tree);
            Lexeme ids = Lexeme.car(table);
            if (table == null) { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
            else { return new Lexeme("BOOLEAN", "true",left.lineNumber);}
        }
        else if (left != null && left.value.equals("null")){
            if (right.value.equals("null")) { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
            else { return new Lexeme("BOOLEAN", "true",left.lineNumber); }
        }
        else if (right != null && right.value.equals("null")){
            if (left.value.equals("null")) { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
            else { return new Lexeme("BOOLEAN", "true",left.lineNumber); }
        }
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER")){
            if (left.getValInteger() != right.getValInteger()){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left.type.equals("REAL") && right.type.equals("REAL")){
            if (left.getValDouble() != right.getValDouble()){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left.type.equals("BOOLEAN") && right.type.equals("BOOLEAN")){
            if (left.value.equals(right.value)){ return new Lexeme("BOOLEAN", "false",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "true",left.lineNumber); }
        }
        else if (left.type.equals("INTEGER") && right.type.equals("REAL")){
            if ((double)left.getValInteger() != right.getValDouble()){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left.type.equals("REAL") && right.type.equals("INTEGER")){
            if (left.getValDouble() != (double)right.getValInteger()){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left.type.equals("STRING") && right.type.equals("STRING")){
            if (left.value.equals(right.value)){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        return tree;
    }
    public static Lexeme evalFewer(Lexeme tree, Lexeme env) throws IOException {
        //eval the left and the right hand sides
        Lexeme left = eval(tree.left,env);
        Lexeme right = eval(tree.right,env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER")){
            if (left.getValInteger() < right.getValInteger()){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left.type.equals("REAL") && right.type.equals("REAL")){
            if (left.getValDouble() < right.getValDouble()){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left.type.equals("INTEGER") && right.type.equals("REAL")){
            if ((double)left.getValInteger() < right.getValDouble()){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left.type.equals("REAL") && right.type.equals("INTEGER")){
            if (left.getValDouble() < (double)right.getValInteger()){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left.type.equals("STRING") && right.type.equals("STRING")){
            if (left.value.equals(right.value)){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        return tree;
    }
    public static Lexeme evalFewerEqual(Lexeme tree, Lexeme env) throws IOException {
        //eval the left and the right hand sides
        Lexeme left = eval(tree.left,env);
        Lexeme right = eval(tree.right,env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER")){
            if (left.getValInteger() <= right.getValInteger()){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left.type.equals("REAL") && right.type.equals("REAL")){
            if (left.getValDouble() <= right.getValDouble()){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left.type.equals("INTEGER") && right.type.equals("REAL")){
            if ((double)left.getValInteger() <= right.getValDouble()){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left.type.equals("REAL") && right.type.equals("INTEGER")){
            if (left.getValDouble() <= (double)right.getValInteger()){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left.type.equals("STRING") && right.type.equals("STRING")){
            if (left.value.equals(right.value)){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        return tree;
    }
    public static Lexeme evalGreater(Lexeme tree, Lexeme env) throws IOException {
        //eval the left and the right hand sides
        Lexeme left = eval(tree.left,env);
        Lexeme right = eval(tree.right,env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER")){
            if (left.getValInteger() > right.getValInteger()){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left.type.equals("REAL") && right.type.equals("REAL")){
            if (left.getValDouble() > right.getValDouble()){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left.type.equals("INTEGER") && right.type.equals("REAL")){
            if ((double)left.getValInteger() > right.getValDouble()){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left.type.equals("REAL") && right.type.equals("INTEGER")){
            if (left.getValDouble() > (double)right.getValInteger()){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left.type.equals("STRING") && right.type.equals("STRING")){
            if (left.value.equals(right.value)){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        return tree;
    }
    public static Lexeme evalGreaterEqual(Lexeme tree, Lexeme env) throws IOException {
        //eval the left and the right hand sides
        Lexeme left = eval(tree.left,env);
        Lexeme right = eval(tree.right,env);
        if (left.type.equals("INTEGER") && right.type.equals("INTEGER")){
            if (left.getValInteger() >= right.getValInteger()){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left.type.equals("REAL") && right.type.equals("REAL")){
            if (left.getValDouble() >= right.getValDouble()){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left.type.equals("INTEGER") && right.type.equals("REAL")){
            if ((double)left.getValInteger() >= right.getValDouble()){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left.type.equals("REAL") && right.type.equals("INTEGER")){
            if (left.getValDouble() >= (double)right.getValInteger()){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        else if (left.type.equals("STRING") && right.type.equals("STRING")){
            if (left.value.equals(right.value)){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        return tree;
    }
    public static Lexeme evalOr(Lexeme tree, Lexeme env) throws IOException {
        //eval the left and the right hand sides
        Lexeme left = eval(tree.left,env);
        Lexeme right = eval(tree.right,env);
        if (left.type.equals("BOOLEAN") && right.type.equals("BOOLEAN")){
            if (left.value.equals("true")){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else if (right.value.equals("true")){ return new Lexeme("BOOLEAN", "true",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "false",left.lineNumber);}
        }
        return tree;
    }
    public static Lexeme evalAnd(Lexeme tree, Lexeme env) throws IOException {
        //eval the left and the right hand sides
        Lexeme left = eval(tree.left,env);
        Lexeme right = eval(tree.right,env);
        if (left.type.equals("BOOLEAN") && right.type.equals("BOOLEAN")){
            if (left.value.equals("false")){ return new Lexeme("BOOLEAN", "false",left.lineNumber); }
            else if (right.value.equals("false")){ return new Lexeme("BOOLEAN", "false",left.lineNumber); }
            else { return new Lexeme("BOOLEAN", "true",left.lineNumber);}
        }
        return tree;
    }
/*Statements */
    public static Lexeme evalVarAssign(Lexeme tree, Lexeme env) throws IOException {
        Lexeme i = getVarAssignIdSeq(tree);
        if (i.type.equals("ID")){
            Lexeme init = getVarAssignInit(tree);
            init = eval(init, env);
            Environment.update(i, init, env);
            return null;
        }
        else if (i.type.equals("IDENTIFIER_SEQUENCE")){
            Lexeme init = getVarAssignInit(tree);
            init = eval(init,env);
            Lexeme object = eval(getLHS(i),env);
            Environment.update(getRHS(i), init, object);
            return null;
        }
        return null;
    }
    public static Lexeme evalIf(Lexeme tree, Lexeme env) throws IOException {
        Lexeme econdition = eval(getIfBoolExpr(tree),env);
        if (econdition.value.equals("true")){
            Lexeme result = eval(getIfBlock(tree),env);
            return result;
        }
        else {
            Lexeme result = eval(getIfElseOpt(tree),env);
            return result;
        }
    }
    public static Lexeme evalElse(Lexeme tree, Lexeme env) throws IOException {
        return eval(Lexeme.car(tree), env);
    }
    public static Lexeme evalWhile(Lexeme tree, Lexeme env) throws IOException {
        Lexeme condition = getWhileBoolExpr(tree);
        Lexeme body = getWhileBlock(tree);
        Lexeme be = eval(condition, env);
        Lexeme ptr = null;
        while (be.value.equals("true")){
            ptr = eval(body, env); 
            if (ptr != null && ptr.type.equals("RETURNED")) return ptr;
            condition = getWhileBoolExpr(tree);
            be = eval(condition, env);
        }
        return null;
    }
    public static Lexeme evalReturn(Lexeme tree, Lexeme env) throws IOException {
        Lexeme finalVal = eval(Lexeme.car(tree),env);
        return Lexeme.cons("RETURNED",finalVal,null);
    }
    public static Lexeme evalImport(Lexeme tree, Lexeme env) throws IOException {
        return null;
    }
/*Sequences */
    public static Lexeme evalParamSeq(Lexeme tree, Lexeme env) throws IOException {
        // return Lexeme.car(tree);
        System.out.println("");
        System.out.println("evalParamSeq() probably shouldn't ever run");
        System.out.println("");
        System.out.println("");
        return null;
    }
    public static Lexeme evalStatementSeq(Lexeme tree, Lexeme env) throws IOException {
        Lexeme result = null;
        while (tree != null){
            result = eval(Lexeme.car(tree),env);
            if (result != null){
                if (result.type.equals("RETURNED")) return result;
            }
            tree = Lexeme.cdr(tree);
        }
        return result;
    }
    public static Lexeme evalImportStatementSeq(Lexeme tree, Lexeme env) throws IOException {
        return null;
    }
    public static Lexeme evalDefSeq(Lexeme tree, Lexeme env) throws IOException {
        eval(Lexeme.car(tree), env);
        eval(Lexeme.cdr(tree), env);
        return null;
    }
    public static Lexeme evalArgsSeq(Lexeme tree, Lexeme env) throws IOException {
        return Lexeme.cons("GLUE",eval(Lexeme.car(tree),env),eval(Lexeme.cdr(tree),env));
    }
    public static Lexeme evalIdSeq(Lexeme tree, Lexeme env) throws IOException {
        Lexeme object = eval(getLHS(tree), env);
        return eval(Lexeme.cdr(tree), object); // objects == environments!
    }

/*Definitions */
    public static Lexeme evalVariableDef(Lexeme tree, Lexeme env) throws IOException {        
        Lexeme i = Lexeme.car(tree);        
        Lexeme o = Lexeme.cdr(tree);
        if (o == null) {
            o = Lexeme.cons("NULL", null, null);
        }
        else{
            o = eval(o, env);
        }
        Environment.insert(i, o,env);
        return tree; 
    }
    public static Lexeme evalMethodDef(Lexeme tree, Lexeme env) throws IOException {
        Lexeme closure =
            Lexeme.cons("CLOSURE",env,
                Lexeme.cons("GLUE",getMethodDefParams(tree),
                    Lexeme.cons("GLUE",getMethodDefBlock(tree),null)));
        Environment.insert(getMethodDefName(tree),closure,env);
        return closure;
    }
    public static Lexeme evalLambda(Lexeme tree, Lexeme env) throws IOException {
        Lexeme closure =
            Lexeme.cons("CLOSURE",env,
                Lexeme.cons("GLUE",getMethodDefParams(tree),
                    Lexeme.cons("GLUE",getMethodDefBlock(tree),null)));
        return closure;
    }
    public static Lexeme evalClassDef(Lexeme tree, Lexeme env) throws IOException {
        Environment.insert(Lexeme.car(tree), Lexeme.cons("OCLOSURE", env, tree),env);
        eval(Lexeme.cdr(tree), env);
        return null;
    }

/*Function calls */
    public static Lexeme evalMethodCall(Lexeme tree, Lexeme env) throws IOException {
        Lexeme method_name = getMethodCallName(tree);
        Lexeme args = getMethodCallArgs(tree); //arguments
        Lexeme eargs = evalArgs(args,env);
        if (method_name.value.equals("print")){ return evalReservedPrint(eargs); }
        if (method_name.value.equals("openFileForReading")){ return evalReservedOpenFileForReading(eargs); }
        if (method_name.value.equals("readInteger")){ return evalReservedReadInteger(eargs); }
        if (method_name.value.equals("atFileEnd")){ return evalReservedAtFileEnd(eargs); }
        if (method_name.value.equals("closeFile")){ return evalReservedCloseFile(eargs); }
        if (method_name.value.equals("newArray")){ return evalReservedNewArray(eargs); }
        if (method_name.value.equals("setArray")){ return evalReservedSetArray(eargs); }
        if (method_name.value.equals("getArray")){ return evalReservedGetArray(eargs); }
        if (method_name.value.equals("getArgCount")){ return evalReservedGetArgCount(eargs); }
        if (method_name.value.equals("getArg")){ return evalReservedGetArg(eargs); }
        Lexeme closure = eval(method_name,env); //identifier FIXME rhs
        Lexeme params = getClosureParams(closure); 
        Lexeme body = getClosureBody(closure);
        Lexeme senv = getClosureEnvironment(closure);
        Lexeme xenv = Environment.extend(params,eargs,senv);
        // insert a variable that points to xenv
        Environment.insert(new Lexeme("ID","this",0), xenv, xenv);
        Lexeme result = eval(body,xenv);
        if (result != null && result.type.equals("RETURNED")) result = Lexeme.car(result);
        return result;
    }
    public static Lexeme evalArgs(Lexeme args, Lexeme env) throws IOException {
        if (args == null){
            return null;
        }
        else{
            return Lexeme.cons("GLUE",eval(Lexeme.car(args),env),eval(Lexeme.cdr(args),env));
        }
    }
/*Reserved Functions */
    public static Lexeme evalReservedPrint(Lexeme eargs){
        Lexeme ptr = eargs;
        Lexeme val = null;
        while (ptr != null){
            val = Lexeme.car(ptr);
            if (val.type.equals("STRING")){ System.out.print(val.value); }
            else PrettyPrint.pp(val);
            ptr = Lexeme.cdr(ptr);
        }
        return new Lexeme("NULL","null",0);
    }
    public static Lexeme evalReservedOpenFileForReading(Lexeme eargs) throws IOException {
        Lexeme fileName = Lexeme.car(eargs);
        Lexeme fp = new Lexeme("FILE_POINTER", fileName.value,0);
        fp.setFilePtr(fileName.value);
        return fp;
    }
    public static Lexeme evalReservedReadInteger(Lexeme eargs)throws IOException {
        Lexeme filePointer = Lexeme.car(eargs);
        return filePointer.readIntFilePtr();
    }
    public static Lexeme evalReservedAtFileEnd(Lexeme eargs)throws IOException {
        Lexeme filePointer = Lexeme.car(eargs);
        if (filePointer.isEndOfFile()){
            return new Lexeme("BOOLEAN","true",0); 
        }
        else{
            return new Lexeme("BOOLEAN","false",0); 
        }
    }
    public static Lexeme evalReservedCloseFile(Lexeme eargs)throws IOException {
        Lexeme filePointer = Lexeme.car(eargs);
        filePointer.closeFile(); //implementation language for closing a file
        return new Lexeme("BOOLEAN","true",0);       //gotta return something
    }
    public static Lexeme evalReservedNewArray(Lexeme eargs) {
        assert (eargs.left == null); //ensure only one argument
        Lexeme size = Lexeme.car(eargs);
        assert(size.type == "INTEGER");          //ensure an integer argument
        Lexeme a = new Lexeme("ARRAY", null,0);
        a.arr = new Lexeme [Integer.parseInt(size.value)]; //allocate the array
        assert(a.arr != null);                //ensure a good allocation
        return a;
    }
    public static Lexeme evalReservedSetArray(Lexeme eargs) {
        //assert(length(eargs) == 3);
        Lexeme a = Lexeme.car(eargs);
        Lexeme i = Lexeme.cadr(eargs);
        Lexeme v = Lexeme.caddr(eargs);
        //check for valid types here
        a.arr[Integer.parseInt(i.value)] = v;
        return v;                      //could also return the previous value
    }
    public static Lexeme evalReservedGetArray(Lexeme eargs) {
        Lexeme a = Lexeme.car(eargs);
        Lexeme i = Lexeme.cadr(eargs);
        //check for valid types here
        Lexeme v =  a.arr[Integer.parseInt(i.value)];
        return v;
    }
    public static Lexeme evalReservedGetArgCount(Lexeme eargs) {
        return new Lexeme("INTEGER",countCL.toString(),0);
    }
    public static Lexeme evalReservedGetArg(Lexeme eargs) {
        Lexeme index = Lexeme.car(eargs);
        return new Lexeme("STRING",argsCL[Integer.parseInt(index.value)],0);
    }

/*Program and function body are parsed as blocks */
    public static Lexeme evalProgram(Lexeme tree, Lexeme env) throws IOException {
        //eval(Lexeme.car(tree), env);
        eval(Lexeme.cdr(tree), env);
        return null;
    }
    public static Lexeme evalBlock(Lexeme tree, Lexeme env) throws IOException {
        Lexeme result = null;
        while (tree != null){
            result = eval(Lexeme.car(tree), env);
            if (result != null){
                if (result.type.equals("RETURNED")){
                    return result;
                }
            }
            tree = Lexeme.cdr(tree);
        }
        return result;
    }
/*Private helper get functions */
    private static Lexeme getWhileBoolExpr(Lexeme tree){
        return Lexeme.car(tree);
    }
    private static Lexeme getWhileBlock(Lexeme tree){
        return Lexeme.cdr(tree);
    }
    private static Lexeme getVarAssignIdSeq(Lexeme tree){
        return Lexeme.car(tree);
    }
    private static Lexeme getVarAssignInit(Lexeme tree){
        return Lexeme.cdr(tree);
    }
    private static Lexeme getMethodDefName(Lexeme tree){
        return Lexeme.car(tree);
    }
    private static Lexeme getMethodDefParams(Lexeme tree){
        return Lexeme.car(Lexeme.cdr(tree));
    }
    private static Lexeme getMethodDefBlock(Lexeme tree){
        return Lexeme.cdr(Lexeme.cdr(tree));
    }
    private static Lexeme getMethodCallName(Lexeme tree){
        return Lexeme.car(tree);
    }
    private static Lexeme getMethodCallArgs(Lexeme tree){
        return Lexeme.cdr(tree);
    }
    private static Lexeme getClosureParams(Lexeme closure){
        return Lexeme.car(Lexeme.cdr(closure));
    }
    private static Lexeme getClosureBody(Lexeme closure){
        return Lexeme.car(Lexeme.cdr(Lexeme.cdr(closure)));
    }
    private static Lexeme getClosureEnvironment(Lexeme closure){
        return Lexeme.car(closure);
    }
    private static Lexeme getIfBoolExpr(Lexeme tree){
        return Lexeme.car(tree);
    }
    private static Lexeme getIfBlock(Lexeme tree){
        return Lexeme.car(Lexeme.cdr(tree));
    }
    private static Lexeme getIfElseOpt(Lexeme tree){
        return Lexeme.cdr(Lexeme.cdr(tree));
    }
    private static Lexeme getRHS(Lexeme tree){
        if (tree.type.equals("ID")) return tree;
        else if (tree.type.equals("IDENTIFIER_SEQUENCE")){
            // must get the far right ID
            Lexeme ptr = tree;
            Lexeme rhs = Lexeme.cdr(ptr);
            while (ptr != null){
                if (rhs == null) return ptr;
                ptr = Lexeme.cdr(ptr);
                rhs = Lexeme.cdr(ptr);
            }
        } 
        return null;
    }
    private static Lexeme getLHS(Lexeme tree){
        if (tree.type.equals("ID")) return tree;
        // must get the far left ID
        else if (tree.type.equals("IDENTIFIER_SEQUENCE")){
            return Lexeme.car(tree);
        }
        return null;
    }
    public static void semanticError(Lexeme errored,String errorMessage) throws IOException {
		System.out.printf("\033[0;31m"); //Set the text to the color red
		System.out.printf("\033[31m----Semantic Error: on line #%d\t%-20s\n",errored.lineNumber,errorMessage);
		System.out.printf("\033[0m"); //Resets the text to default color
		throw(new IOException("illegal"));
	}

/*Debug function */  
    public static Lexeme find(Lexeme root,String name){
        if (root.type.equals(name)) return root;
        Lexeme ptr = null;
        if (root.left != null) ptr = find(root.left,name);
        if (ptr != null) return ptr;
        if (root.right != null) ptr = find(root.right,name);
        return ptr;
    }
/*Drivier function */  
	public static void main(String[] args) throws IOException {
        argsCL = args;
        countCL = args.length;
        Lexeme env = Environment.create();
        Parser p = new Parser(args[0]);
        Lexeme tree = p.parse();
        eval(tree,env);
        Lexeme call = Lexeme.cons("METHOD_CALL", new Lexeme("ID", "main", 0), null);
        eval(call,env);
    }
}

	

