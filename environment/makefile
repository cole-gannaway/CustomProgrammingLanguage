JCC = javac

JFLAGS = -g

default: Environment.class Lexeme.class

environment: Environment.class Lexeme.class

test: default
	java Environment

Environment.class : Environment.java
	$(JCC) $(JFLAGS) Environment.java

Lexeme.class: Lexeme.java
	$(JCC) $(JFLAGS) Lexeme.java

clean: 
	$(RM) *.class
	clear
