.data
 _base:	.word 0
 _exponente:	.word 0
 _res:	.word 0
.text
.globl main
potencia:
li $t0, 1
sw $t0, _pot
li $t1, 1
sw $t1, _i
L3:
lw $t2, _i
lw $t3, _exponente
ble $t2, $t3, L1
b L2
L1:
lw $t2, _pot
lw $t3, _base
mult $t2, $t3
mflo $t2
sw $t2, _pot
lw $t3, _i
li $t4, 1
add $t5, $t3, $t4
sw $t5, _i
b L3
L2:
lw $t3, _pot
main:
move $fp, $sp
li $t4, 2
sw $t4, _base
li $t6, 3
sw $t6, _exponente
lw $t7, _base
lw $t8, _exponente
move $a0, $t13
move $a1, $t14
jal potencia
move $v0, _res
li $v0, 10
syscall
