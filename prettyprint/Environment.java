import java.io.IOException;
import java.util.LinkedList; 
import java.util.Queue; 

public class Environment {
	public static Lexeme create(){
		return Lexeme.cons("ENV",Lexeme.cons("TABLE",null,null),null);
	}
	public static Lexeme lookup(Lexeme env, Lexeme id){
        while (env != null){
            Lexeme table = Lexeme.car(env);
            Lexeme ids = Lexeme.car(table);
            Lexeme vals = Lexeme.cdr(table);
            if (ids == null) return null;
            ids = Lexeme.cdr(ids);
			vals = Lexeme.cdr(vals);
            while (ids != null){
                if (Lexeme.isSameValue(id,ids)){
                    return vals;
				}
				ids = Lexeme.cdr(ids);
				vals = Lexeme.cdr(vals);
			}
            env = Lexeme.cdr(env);
        }
        try {
			undefinedVar(id);
		}
		catch (IOException e){
   		}
        //Fatal("variable ",variable," is undefined");
        return null;
    }
  	public static void undefinedVar(Lexeme id) throws IOException {
		System.out.printf("\033[0;31m"); //Set the text to the color red
		System.out.println("variable " + id.value + " is undefined");
		System.out.printf("\033[0m"); //Resets the text to default color
  		throw(new IOException("variable undefined"));
  	}


	public static Lexeme update(Lexeme env, Lexeme id, String value){
		Lexeme result = lookup(env,id);
		if (result == null) return null;
		result.value = value;
		return result;
	}
	public static Lexeme insert(Lexeme env,Lexeme newId,Lexeme newValue){
		Lexeme table = Lexeme.car(env);
		if (Lexeme.car(table) == null){
			Lexeme.setCar(table,Lexeme.cons("IDS",newId,Lexeme.car(table))); //setLexemeLeft
        	Lexeme.setCdr(table,Lexeme.cons("VALUES",newValue,Lexeme.cdr(table))); //setLexemeRight	
		}
		Lexeme ids = Lexeme.car(table);
        Lexeme vals = Lexeme.cdr(table);

        Lexeme.setCdr(newId,Lexeme.cdr(ids));
        Lexeme.setCdr(newValue,Lexeme.cdr(vals));
	
		Lexeme.setCdr(ids,newId);
		Lexeme.setCdr(vals,newValue);
        return newValue;
	}
	
	/*public static Lexeme extend(Lexeme env, Lexeme ids,Lexeme vals){
        return Lexeme.cons("ENV",Lexeme.cons("TABLE",ids,vals),env);
    }*/
    public static Lexeme extend(Lexeme env){
    	Lexeme new_env = create();
    	Lexeme.setCdr(new_env,env);
    	return new_env;
    }
      
/*Template
	public static Lexeme nameOffunction(){

	}
*/
	public static void display_local (Lexeme env, int i){
		System.out.println("Table " + i + ":");
		Lexeme table = Lexeme.car(env);
		Lexeme ids = Lexeme.car(table);
		//System.out.println(ids.type + " " + ids.lineNumber);
		Lexeme vals = Lexeme.cdr(table);
		//System.out.println(vals.type + " " + vals.lineNumber);
		if (ids == null) return;
		ids = Lexeme.cdr(ids);
		vals = Lexeme.cdr(vals);
		while (ids != null){
			System.out.print("\t" + ids.value + " = "); vals.displayValue(); System.out.println();
			ids = Lexeme.cdr(ids);
			vals = Lexeme.cdr(vals);
		}
	}
	public static void display_all(Lexeme env){
		Lexeme ptr = env;
		int i = 0;
		while (ptr != null){
			display_local(ptr,i);
			ptr = Lexeme.cdr(ptr);
			i++;
		}
	}
	public static void main(String[] args) throws IOException {
		System.out.println("Creating a new environment...");
		System.out.println("The environment is global_env...");
			Lexeme global_env = create();
				Lexeme var1 = new Lexeme("INTEGER","1",12);
				Lexeme id1 = new Lexeme("ID","one",12);
				Lexeme var2 = new Lexeme("INTEGER","2",22);
				Lexeme id2 = new Lexeme("ID","two",22);
				Lexeme var3 = new Lexeme("STRING","This is a string",33);
				Lexeme id3 = new Lexeme("ID","string",33);
		System.out.print("Adding variable " + id1.value + " with value "); var1.displayValue(); System.out.println(" to global_env");
		insert(global_env,id1,var1);
		System.out.print("Adding variable " + id2.value + " with value "); var2.displayValue(); System.out.println(" to global_env");
		insert(global_env,id2,var2);
		System.out.print("Adding variable " + id3.value + " with value "); var3.displayValue(); System.out.println(" to global_env");
		insert(global_env,id3,var3);
		
		System.out.println("Displaying global_env...");
		display_local(global_env,0);
		
		System.out.println("Creating a new environment...");
		System.out.println("The environment is local_env...");
			Lexeme local_env = extend(global_env);
				Lexeme var4 = new Lexeme("DOUBLE","4.0",44);
				Lexeme id4 = new Lexeme("ID","four",44);
		System.out.print("Adding variable " + id4.value + " with value "); var4.displayValue(); System.out.println(" to local_env");
		insert(local_env,id4,var4);
		
		System.out.println("Displaying all environments...");
		display_all(local_env);

		Lexeme result = null;
		System.out.println("Looking up variable " + id4.value + " in local_env...");
		result = lookup(local_env,id4);

		String newValue = "0";
		System.out.println("Updating variable " + id4.value + " in local_env to " + newValue);
		result = update(local_env,id4,newValue);
		if (result != null){
			System.out.println("Successfully updated " + id4.value + " to " + newValue);
		}

			Lexeme var5 = new Lexeme("DOUBLE","5.0",55);
			Lexeme id5 = new Lexeme("ID","five",55);
		System.out.println("Looking up variable " + id5.value + " in local_env...");
		result = lookup(local_env,id5);

		System.out.println("Displaying all environments...");
		display_all(local_env);
	}
}
