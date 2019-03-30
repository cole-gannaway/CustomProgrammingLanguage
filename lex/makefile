JCC = javac

JFLAGS = -g

default: Scanner.class Lexer.class Lexeme.class

scanner: Scanner.class Lexer.class Lexeme.class

test1: default
	java Scanner program1.txt

test2: default
	java Scanner program2.txt

test3: default
	java Scanner program3.txt

test4: default
	java Scanner program4.txt

test5: default
	java Scanner program4.txt

testall:
	make test1
	make test2
	make test3
	make test4
	make test5

Scanner.class: Scanner.java
	$(JCC) $(JFLAGS) Scanner.java

Lexer.class: Lexer.java
	$(JCC) $(JFLAGS) Lexer.java

Lexeme.class: Lexeme.java
	$(JCC) $(JFLAGS) Lexeme.java

clean: 
	$(RM) *.class
