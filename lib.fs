: TRUE 1 ;
: FALSE 0 ;
: CR 10 EMIT ;
: 2DUP (n1 n2 -- n1 n2 n1 n2) OVER OVER ;
: IF COMPILE ?BRANCH ?>MARK ; IMMEDIATE
: ELSE COMPILE BRANCH  1 + ?>RESOLVE ?>MARK ; IMMEDIATE
: THEN ?>RESOLVE ; IMMEDIATE
: BEGIN ?<MARK ; IMMEDIATE
: UNTIL COMPILE ?BRANCH ?<RESOLVE ; IMMEDIATE
: DO ?<MARK COMPILE 2DUP COMPILE >R COMPILE >R COMPILE > COMPILE ?BRANCH  ?>MARK ; IMMEDIATE
: LOOP COMPILE R> COMPILE R> COMPILE 1 COMPILE + COMPILE BRANCH 1 + ?>RESOLVE ?<RESOLVE COMPILE R> COMPILE R> COMPILE DROP COMPILE DROP ; IMMEDIATE
: WORDS SIZE 0 DO R> R>  DUP PRINTWORD >R >R LOOP ;
: INTERPRET ( -- ForthMainLoop) BEGIN 62 EMIT PARSE RUN FALSE  UNTIL ;