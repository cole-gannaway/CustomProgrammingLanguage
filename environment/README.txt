Environment -- Created By Cole Gannaway

Makefile Commands:
	Compile using:
		make
		make environment
	Test using:
		make test

Environment:
	create()
		Returns a new environment with a Table lexeme and null as the left and right pointer respectively.
	lookup()
		Searches the most local environment first and then all other environments for an id name. Returns the value if found, else returns null.
	update()
		Searches the most local environment first and then all other environments for an id name. Updates its corresponding value to a new value. Returns the new value if found, else returns null.
	insert()
		Inserts a new value lexeme and new id lexeme into the specified environment and returns the new value.
	extend()
		Returns a new environment with a Table lexeme and the previous environment as the left and right pointer respectively
