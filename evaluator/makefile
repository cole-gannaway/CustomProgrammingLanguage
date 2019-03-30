JCC = javac

JFLAGS = -g

default: Evaluator.class Environment.class PrettyPrint.class Parser.class Lexer.class Lexeme.class

run: Evaluator.class Environment.class PrettyPrint.class Parser.class Lexer.class Lexeme.class

all: default
	make error1
	make error1x -i
	make error2
	make error2x -i
	make error3
	make error3x -i
	make error4
	make error4x -i
	make error5
	make error5x -i
	make arrays
	make arraysx
	make conditionals
	make conditionalsx
	make recursion
	make recursionx
	make iteration
	make iterationx
	make functions
	make functionsx	  # shows you can pass functions and return nested functions
	make lambda	  
	make lambdax	  
	make objects	  
	make objectsx		# get and update field, method call (e.g. setter)
	make problem
	make problemx

error1 : 
	cat error1.cole

error1x : 
	java Evaluator error1.cole

error2 : 
	cat error2.cole

error2x : 
	java Evaluator error2.cole

error3 : 
	cat error3.cole

error3x : 
	java Evaluator error3.cole

error4 : 
	cat error4.cole

error4x : 
	java Evaluator error4.cole

error5 : 
	cat error5.cole

error5x : 
	java Evaluator error5.cole

arrays : 
	cat arrays.cole

arraysx : 
	java Evaluator arrays.cole

conditionals : 
	cat conditionals.cole

conditionalsx : 
	java Evaluator conditionals.cole
	
recursion : 
	cat conditionals.cole

recursionx : 
	java Evaluator recursion.cole
	
iteration : 
	cat iteration.cole

iterationx : 
	java Evaluator iteration.cole
	
functions : 
	cat functions.cole

functionsx :  # shows you can pass functions and return nested functions : 
	java Evaluator functions.cole

lambda   : 
	cat lambda.cole

lambdax   : 
	java Evaluator lambda.cole

objects   : 
	cat objects.cole
	
objectsx : # get and update field, method call (e.g. setter)
	java Evaluator objects.cole
 
problem :
	cat iAVL.cole

problemx : 
	java Evaluator iAVL.cole problem.ints

Evaluator.class: Evaluator.java
	$(JCC) $(JFLAGS) Evaluator.java

Environment.class : Environment.java
	$(JCC) $(JFLAGS) Environment.java

PrettyPrint.class: PrettyPrint.java
	$(JCC) $(JFLAGS) PrettyPrint.java

Parser.class: Parser.java
	$(JCC) $(JFLAGS) Parser.java

Lexer.class: Lexer.java
	$(JCC) $(JFLAGS) Lexer.java

Lexeme.class: Lexeme.java
	$(JCC) $(JFLAGS) Lexeme.java

clean: 
	$(RM) *.class *.pp*
