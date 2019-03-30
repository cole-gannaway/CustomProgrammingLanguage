# CustomProgramLang

Created my own custom programming language that is very similar to javascript. Every variable is a first-class object including functions. 

Syntax
 * Functions
    * All function defintions begin with the keyword function.
    * Functions can take any number of parameters or none at all.
       * Example
          * function main () {}
 * Variables
    * All variables begin with the keyword var.
    * Variables are dynamically typed and can be Integers, Reals, Booleans, null, Functions, Lamdas, Objects, and more.

Reserved words
 * import
    * delineates the beginning of an import defintion
 * class
    * delineates the beginning of a class defintion
 * function
    * delineates the beginning of a method defintion
 * lambda
    * delineates the beginning of an anonymous method defintion
 * var
    * delineates the beginning of a variable defintion (dynamically typed)
 * endl
    * variable that means newline
 * null
    * the value null
 * if
    * delineates the beginning of a base case conditional
 * else if
    * delineates the beginning of another case conditional
 * else 
    * delineates the beginning the default conditional
 * while
    * delineates the beginning of a loop conditional
 * true
    * boolean value true
 * false
    * boolean value true

Reserved functions
 * print();
    * Examples
       * print("Hello World");
       * print("Hello World", "!","!", endl);
 * openFileForReading(fileName);
    * opens a file a returns its file pointer
 * readInteger(filePointer);
    * reads an integer from the given file pointer
 * atFileEnd(filePointer);
    * returns true or false if a given file pointer is at the end of the file
 * closeFile(filePointer);
    * closes a file using the given file pointer
 * newArray(size);
    * creates and returns a new Lexeme array of a given size
 * setArray(arr,index,val);
    * sets a given array's index to a given val
 * getArray(arr,index);
    * gets a given array's index
 * getArgCount();
    * returns the number of command line arguments
 * getArg(index);
    * returns the command line argument at a given index