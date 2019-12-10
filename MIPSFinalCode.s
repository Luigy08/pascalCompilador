.data
 _a:	.word 0
 _b:	.space 40
 _c:	.word 0
 _msg1:	.asciiz "asd"
.text
.globl main
main:
move $fp, $sp
li $t0, 1
li $t1, 2
add $t2, $t0, $t1
li $t0, 1
sub $t1, $t2, $t0
li $t0, 4
mult $t1, $t0
mflo $t0
la null, _b
add $t1, $t0, null
li $v0, 4
la $a0, _msg1
syscall
li $v0, 1
lw $a0, ($t1)
syscall
li $v0, 10
syscall
