CR ." (Run unit testing) " CR
1 1 +
 ( 测试空白字符  )
			: add1      1 	+ ;
add1
: add2 add1 add1 ;
add2
.
( 测试出错  )
1 2 3 *(@&#*$(  
: XXX IF + + ELSE + + + + THEN [ .s ] 1 - ;
: tt DO .s LOOP ;
1 2 3 TRUE XXX
7 8 9 10 FALSE XXX
.
: YYY BEGIN .s TRUE UNTIL ;
( 真假标志为FALSE时无限循环  )
3 YYY 
( 测试变量，应在栈上留下555，变量的初始偏移量是0，这里与传统forth不同  )
VARIABLE  ZZ 555 0 ZZ ! 0 ZZ @ 
: average DUP >R 1 DO + LOOP R> / ;
( 测试数组  )
CREATE weekrain 7 ALLOT 
( 测试写入数组  )
777 0 weekrain ! 888 1 weekrain !
( 将数组偏移量设置为常数  ) 
0 CONSTANT Monday 
( 将数组偏移量设置为常数  )
1 CONSTANT Tuesday 
( 测试读取数组，在栈上留下777 888 0  )
Monday weekrain @ Tuesday weekrain @ Tuesday 1+ weekrain @ .s