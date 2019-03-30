JCC = javac

JFLAGS = -g

default: PrettyPrint.class Parser.class Lexer.class Lexeme.class

run: PrettyPrint.class Parser.class Lexer.class Lexeme.class

test1: default
	echo Original file:
	cat test1.mylang
	echo Pretty Printed version of the original:
	pp test1.mylang > test1.pp.1
	cat test1.pp.1
	echo Pretty Printed version of the pretty printed version:
	pp test1.pp.1 > test1.pp.2
	cat test1.pp.2
	diff -s -q test1.pp.1 test1.pp.2

test2: default
	echo Original file:
	cat test2.mylang
	echo Pretty Printed version of the original:
	pp test2.mylang > test2.pp.2
	cat test2.pp.2
	echo Pretty Printed version of the pretty printed version:
	pp test2.pp.2 > test2.pp.2
	cat test2.pp.2
	diff -s -q test2.pp.2 test2.pp.2

test3: default
	echo Original file:
	cat test3.mylang
	echo Pretty Printed version of the original:
	pp test3.mylang > test3.pp.3
	cat test3.pp.3
	echo Pretty Printed version of the pretty printed version:
	pp test3.pp.3 > test3.pp.3
	cat test3.pp.3
	diff -s -q test3.pp.3 test3.pp.3

testall: default
	make test1
	make test2
	make test3

recognizer :
	recognizer test1.mylang
	recognizer test2.mylang
	recognizer test3.mylang
	recognizer bad_program.mylang

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
