CR ." (Run unit testing) " CR
1 1 +
 ( ���Կհ��ַ�  )
			: add1      1 	+ ;
add1
: add2 add1 add1 ;
add2
.
( ���Գ���  )
1 2 3 *(@&#*$(  
: XXX IF + + ELSE + + + + THEN [ .s ] 1 - ;
: tt DO .s LOOP ;
1 2 3 TRUE XXX
7 8 9 10 FALSE XXX
.
: YYY BEGIN .s TRUE UNTIL ;
( ��ٱ�־ΪFALSEʱ����ѭ��  )
3 YYY 
( ���Ա�����Ӧ��ջ������555�������ĳ�ʼƫ������0�������봫ͳforth��ͬ  )
VARIABLE  ZZ 555 0 ZZ ! 0 ZZ @ 
: average DUP >R 1 DO + LOOP R> / ;
( ��������  )
CREATE weekrain 7 ALLOT 
( ����д������  )
777 0 weekrain ! 888 1 weekrain !
( ������ƫ��������Ϊ����  ) 
0 CONSTANT Monday 
( ������ƫ��������Ϊ����  )
1 CONSTANT Tuesday 
( ���Զ�ȡ���飬��ջ������777 888 0  )
Monday weekrain @ Tuesday weekrain @ Tuesday 1+ weekrain @ .s