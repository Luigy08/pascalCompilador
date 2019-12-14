.data
 _a:	.word 0
 _b:	.word 0
 _c:	.word 0
 _d:	.word 0
 _e:	.word 0
 _asd:	.word 0
.text
.globl main
x2:
li $t0, 1
sw $t0, _a
lw $t1, _a
li $t2, 2
beq $t1, $t2, L1
b L2
L1:
li $t1, 1
sw $t1, _e
b L2
L2:
li $t2, 1
li $t3, 2
bgt $t2, $t3, L3
b L4
L3:
li $t2, 3
li $t3, 4
bgt $t2, $t3, L5
b L4
L5:
li $t2, 1
b L6
L4:
li $t3, 0
L6:
main:
move $fp, $sp
li $t4, 1
sw $t4, _a
li $v0, 5
syscall
sw $v0, _a
li $t5, 1
li $t6, 2
bgt $t5, $t6, L7
b L8
L7:
li $t5, 3
li $t6, 4
bgt $t5, $t6, L9
b L8
L9:
li $t5, 1
b L10
L8:
li $t6, 0
L10:
li $t7, 1
li $t8, 2
mult $t7, $t8
mflo $t7
move $a0, $t15
move $a1, $t18
move $a2, $t19
jal x2
move $v0, _e
li $v0, 10
syscall
