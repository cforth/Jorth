( Forth System Words Begin )
: TRUE 1 ;
: FALSE 0 ;
: CR 10 EMIT ;
: SPACE 32 EMIT ;

: 1+ 1 + ;
: 1- 1 - ;
: 2+ 2 + ;
: 2- 2 - ;
: 2* 2 * ;
: 2/ 2 / ;

: DUP 1 PICK ;
: SWAP 2 ROLL ;
: OVER 2 PICK ;
: ROT 3 ROLL ;
: -ROT 3 ROLL 3 ROLL ;
: 2DUP ( n1 n2 -- n1 n2 n1 n2 ) OVER OVER ;
: 2DROP DROP DROP ;
: 2SWAP 4 ROLL 4 ROLL ;
: 2OVER 4 PICK 4 PICK ;
: 2ROT 6 ROLL 6 ROLL ;
: NIP SWAP DROP ;
: TUCK SWAP OVER ;
: 3DUP 3 PICK 3 PICK 3 PICK ;

: IF COMPILE ?BRANCH ?>MARK ; IMMEDIATE
: ELSE COMPILE BRANCH  1+ ?>RESOLVE ?>MARK ; IMMEDIATE
: THEN ?>RESOLVE ; IMMEDIATE
: BEGIN ?<MARK ; IMMEDIATE
: UNTIL COMPILE ?BRANCH ?<RESOLVE ; IMMEDIATE
: DO ?<MARK COMPILE 2DUP COMPILE >R COMPILE >R COMPILE > COMPILE ?BRANCH  ?>MARK ; IMMEDIATE
: LOOP COMPILE R> COMPILE R> COMPILE 1+ COMPILE BRANCH 1+ ?>RESOLVE ?<RESOLVE COMPILE R> COMPILE R> COMPILE 2DROP ; IMMEDIATE

: MOD ( N1 N2 -- MOD ) 2DUP / * - ;
: /MOD ( N1 N2 -- MOD DIV ) 2DUP / >R MOD R> ;
: NEGATE ( N -- -N ) -1 * ;
: ABS DUP 0 < IF NEGATE THEN ;
: MAX 2DUP < IF SWAP THEN DROP ;
: MIN 2DUP > IF SWAP THEN DROP ;
: MAX2 ( N1 N2 N3 -- MAX1 MAX2 ) 2DUP MAX >R MIN MAX R> ;

: 0= 0 = ;
: <> 0= IF FALSE ELSE TRUE THEN ;
: 0<> 0 <> ;
: 0> 0 > ;
: 0< 0 < ;
: ?DUP DUP 0<> IF DUP THEN ;

: WORDS
	." (Dictionary Start)" CR
	SIZE 0 
	DO
		R> R> DUP PRINTWORD SPACE SPACE >R >R
	LOOP CR ." (Dictionary End)" CR ;

: QUIT ( -- ForthMainLoop)
	." (MAIN_LOOP START)" CR
	BEGIN
		." >"
		QUERY
		RUN
		FALSE
	UNTIL ;

( Forth System Words End )
." Jorth -- Forth system by Java, Realized by Chaif!" CR
." This is all system words in dictionary, Enter 'SEE WordName' view word, enter 'BYE' exit! " CR
WORDS