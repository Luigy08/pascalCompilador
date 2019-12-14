.data
 _a:	.word 0
 _b:	.word 0
 _c:	.space 40
 _e:	.word 0
 _msg1:	.asciiz "meow"
 _msg2:	.asciiz "write"
 _msg3:	.asciiz "watever"
 _msg4:	.asciiz "das"
.text
.globl main
main:
move $fp, $sp
li $t0, 10
li $t1, 1
li $t2, 1
sub $t3, $t1, $t2
li $t1, 4
mult $t3, $t1
mflo $t1
la $t2, _c
add $t2, $t1, $t2
sw $t0, ($t2)
li $t0, 0
li $t1, 1
beq $t0, $t1, L1
b L2
L1:
li $t0, 1
li $t1, 1
add $t3, $t0, $t1
sw $t3, _a
li $v0, 4
la $a0, _msg1
syscall
b L3
L2:
li $t0, 1
li $t1, 1
sub $t4, $t0, $t1
li $t0, 4
mult $t4, $t0
mflo $t0
la $t2, _c
add $t1, $t0, $t2
li $t0, 1
bgt $t1, $t0, L4
b L5
L4:
li $t0, 3
li $t1, 4
blt $t0, $t1, L6
b L5
L6:
li $v0, 4
la $a0, _msg2
syscall
b L3
L5:
li $v0, 4
la $a0, _msg3
syscall
b L3
L3:
li $v0, 4
la $a0, _msg4
syscall
li $v0, 10
syscall
