Lexer -- Created By Cole Gannaway

Makefile Commands:
	Compile using:
		make scanner
	Test (individually) using:
		make test1
		make test2
		make test3
		make test4
		make test4
	Test (automatically) using:
		make testall

Lexer Rules: 
	Keywords:
		Key words are case sensitive
			Key words include:
				import
				var
				while
				if
				else

	Loops:
		There are no for loops

	Comments:
		Only block comments are allowed (in-line comments must be used like block comments)
			For example: /* ///// ****** */ 
		Nested comments are not allowed
			Good example: 	/* Created by:
								Cole Gannaway
							*/
			Bad example: 	/* Created by /* Cole Gannaway */ */
