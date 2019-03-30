import java.io.IOException;

public class Environment {
	public static Lexeme create(){
		return Lexeme.cons("ENV",Lexeme.cons("TABLE",null,null),null);
	}
	public static Lexeme lookup(Lexeme variable, Lexeme env){
		while (env != null)
            {
            Lexeme table = Lexeme.car(env);
            Lexeme vars = Lexeme.car(table);
            Lexeme vals = Lexeme.cdr(table);
            while (vars != null)
                {
                if (Lexeme.isSameValue(variable,Lexeme.car(vars)))
                    {
                    return Lexeme.car(vals);
                    }
                vars = Lexeme.cdr(vars);
                vals = Lexeme.cdr(vals);
                }
            env = Lexeme.cdr(env);
            }
        try {
			undefinedVar(variable);
		}
		catch (IOException e){
   		}
        return null;
    }
  	public static void undefinedVar(Lexeme id) throws IOException {
		System.out.printf("\033[0;31m"); //Set the text to the color red
		System.out.println("variable " + id.value + " is undefined");
		System.out.printf("\033[0m"); //Resets the text to default color
  		throw(new IOException("variable undefined"));
	  }
	  public static Lexeme update(Lexeme variable, Lexeme newVal ,Lexeme env){
		while (env != null)
            {
            Lexeme table = Lexeme.car(env);
            Lexeme vars = Lexeme.car(table);
            Lexeme vals = Lexeme.cdr(table);
            while (vars != null)
                {
                if (Lexeme.isSameValue(variable,Lexeme.car(vars)))
                    {
					Lexeme.setCar(vals, newVal);
                    return newVal;
                    }
                vars = Lexeme.cdr(vars);
                vals = Lexeme.cdr(vals);
                }
            env = Lexeme.cdr(env);
            }
        try {
			undefinedVar(variable);
		}
		catch (IOException e){
   		}
        return null;
    }
	public static Lexeme insert(Lexeme variable,Lexeme value,Lexeme env){
		Lexeme table = Lexeme.car(env);
		Lexeme.setCar(table,Lexeme.cons("GLUE",variable,Lexeme.car(table)));
        Lexeme.setCdr(table,Lexeme.cons("GLUE",value,Lexeme.cdr(table)));
        return value;
	}
    public static Lexeme extend(Lexeme variables,Lexeme values,Lexeme env){
        return Lexeme.cons("ENV",Lexeme.cons("TABLE",variables,values),env);
    }
      
/*Template
	public static Lexeme nameOffunction(){

	}
*/
	public static void display_local (Lexeme env, int i){
		System.out.println("Table " + i + ":");
		Lexeme table = Lexeme.car(env);
		Lexeme ids = Lexeme.car(table);
		Lexeme vals = Lexeme.cdr(table);
		Lexeme var = null;
		Lexeme val = null;
		if (ids == null) return;
		while (ids != null){
			var = Lexeme.car(ids);
			val = Lexeme.car(vals);
			System.out.print("\t" + var.value + " = "); 
			if (val.value != null) val.displayValue();
			else val.displayType();
			System.out.println();
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
		insert(id1,var1,global_env);
		System.out.print("Adding variable " + id2.value + " with value "); var2.displayValue(); System.out.println(" to global_env");
		insert(id2,var2,global_env);
		System.out.print("Adding variable " + id3.value + " with value "); var3.displayValue(); System.out.println(" to global_env");
		insert(id3,var3,global_env);
		System.out.println("Displaying global_env...");
		display_local(global_env,0);
		 
		System.out.println("Creating a new environment...");
		System.out.println("The environment is local_env...");
		
			Lexeme local_env = extend(null,null,global_env);
				Lexeme var4 = new Lexeme("DOUBLE","4.0",44);
				Lexeme id4 = new Lexeme("ID","four",44);
		System.out.print("Adding variable " + id4.value + " with value "); var4.displayValue(); System.out.println(" to local_env");
		insert(id4,var4,local_env);
		
		System.out.println("Displaying all environments...");
		display_all(local_env);
		
		Lexeme result = null;
		System.out.println("Looking up variable " + id4.value + " in local_env...");
		result = lookup(id4,local_env);
		result.debug();


		
			Lexeme newValue = new Lexeme("INTEGER","1",44);
		System.out.println("Updating variable " + id4.value + " in local_env to " + newValue.value); 
		
		result = update(id4,newValue,local_env);
	
		if (result != null){
			System.out.println("Successfully updated " + id4.value + " to " + newValue);
		}
			Lexeme var5 = new Lexeme("DOUBLE","5.0",55);
			Lexeme id5 = new Lexeme("ID","five",55);
		System.out.println("Looking up variable " + id5.value + " in local_env...");
		result = lookup(id5,local_env);

		System.out.println("Displaying all environments...");
		display_all(local_env);
	}
}