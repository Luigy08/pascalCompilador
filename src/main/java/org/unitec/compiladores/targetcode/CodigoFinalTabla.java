/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unitec.compiladores.targetcode;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.unitec.compiladores.intermediatecode.Cuadruplo;

/**
 *
 * @author Luis Lainez
 */
public class CodigoFinalTabla {
    List<String> data = new ArrayList();
    List<String> text = new ArrayList();
    ArrayList<Integer> pila = new ArrayList();
    
    public CodigoFinalTabla(){
        data.add(".data");
        text.add(".text");
        text.add(".globl main");
    }
    
    int messageCount = 0;
    
    public String addDataMessage(String message){
        String varName = "_msg" + ++messageCount;
        String varType = "asciiz";
        String varValue = message.replaceAll("'", "\"");
        String row = " %s:\t.%s %s";
        data.add(String.format(row, varName, varType, varValue));
        return varName;
    }
    
    public void addIntegerDataVariable(String varName){
        varName = "_" + varName;
        String varType = "word";
        String varValue = "0";
        String row = " %s:\t.%s %s";
        data.add(String.format(row, varName, varType, varValue));
    }
    
    public void addCharDataVariable(String varName){
        varName = "_" + varName;
        String varType = "byte";
        String varValue = "' '";
        String row = " %s:\t.%s %s";
        data.add(String.format(row, varName, varType, varValue));
    }
    
    

    void addArrayDataVariable(String varName, int space) {
        varName = "_" + varName;
        String varType = "space";
        String varValue = space + "";
        String row = " %s:\t.%s %s";
        data.add(String.format(row, varName, varType, varValue));
    }
    
    public void addWriteStringStatement(String variableName){
        String row = "%s %s, %s";
        text.add(String.format(row, "li", "$v0", "4"));
        text.add(String.format(row, "lw", "$a0", "_"+variableName));
        text.add("syscall");
    }
    
    public void addLabel(String arg){
        text.add(arg + ":");
    }
            
    public void print(){
        for (String dataRow : data) {
            System.out.println(dataRow);
        }
    }

    public void generateFile(String FileName) throws IOException{
        Path file = Paths.get(FileName);
        data.addAll(data.size(), text);
        Files.write(file, data, Charset.forName("UTF-8"));
    }
    
    
    void addWriteStatementLiteral(String messageName) {
        String row = "%s %s, %s";
        text.add(String.format(row, "li", "$v0", "4"));
        text.add(String.format(row, "la", "$a0", messageName));
        text.add("syscall");
    }

    void addWriteStatement(String variableName) {
        String row = "%s %s, %s";
        text.add(String.format(row, "li", "$v0", "1"));
        text.add(String.format(row, "lw", "$a0", "_"+variableName));
        text.add("syscall");
    }

    void generateEndOfProgram() {
        text.add("li $v0, 10");
        text.add("syscall");
    }

    void addReadStatement(String variable) {
        String row = "%s %s, %s";
        text.add(String.format(row, "li", "$v0", "5"));
        text.add("syscall");
        text.add(String.format(row, "sw", "$v0", "_" + variable));
    }

    void generateAssignTemp(String resultado, String temp) {
        String row = "%s %s, %s";
        text.add(String.format(row, "sw", temp, "_" + resultado));
    }

    void generateAddi(String newArg1, String arg2, String resultTemp) {
        String row = "%s %s, %s, %s";
        text.add(String.format(row, "addi", resultTemp, newArg1, arg2));
    }

    void generateAssignVarStore(String variable, String storeTemp) {
        String row = "%s %s, %s";
        text.add(String.format(row, "sw", storeTemp, "_"+variable));
    }

    void generateAssignNum(String arg1, String storeTemp) {
        String row = "%s %s, %s";
        text.add(String.format(row, "li", storeTemp, arg1));
    }

    void generateAdd(String Arg1, String Arg2, String resultado) {
        String row = "%s %s, %s, %s";
        text.add(String.format(row, "add", resultado, Arg1, Arg2));
    }

    void generateAssignVarLoad(String Arg1, String Arg2) {
        String row = "%s %s, %s";
        text.add(String.format(row, "lw", Arg2, "_"+Arg1));
    }

    void generateSub(String Arg1, String Arg2, String tempAvailable) {
        String row = "%s %s, %s, %s";
        text.add(String.format(row, "sub", tempAvailable, Arg1, Arg2));
    }

    void generateMove(String tempArg1, String tempResultado) {
        String row = "%s %s, %s";
        text.add(String.format(row, "move", tempArg1, tempResultado));
    }

    void generateMult(String Arg1, String Arg2) {
        String row = "%s %s, %s";
        text.add(String.format(row, "mult", Arg1, Arg2));
    }

    void generateMFLo(String ArgResultado) {
        String row = "%s %s";
        text.add(String.format(row, "mflo", ArgResultado));
    }

    void generateDiv(String Arg1, String Arg2) {
        String row = "%s %s, %s";
        text.add(String.format(row, "div", Arg1, Arg2));
    }

    void generateReadArray(String arg1, String arg2, String arrayAddress, String tempResultado) {
        String row = "%s %s, %s";
        String row2 = "%s %s, %s, %s";
        text.add(String.format(row, "la", arrayAddress , "_"+arg1));
        text.add(String.format(row2, "add", tempResultado, arg2, arrayAddress));
    }

    void generateWriteArray(String indice, String valor, String resultado, String arrayAddress) {
        String row = "%s %s, %s";
        String row2 = "%s %s, %s, %s";
        text.add(String.format(row, "la", arrayAddress , "_"+resultado));
        text.add(String.format(row2, "add", arrayAddress, indice, arrayAddress));
        text.add(String.format(row, "sw", valor, "("+arrayAddress+")"));
    }

    void addWriteTemp(Cuadruplo C, String temp) {
        String row = "%s %s, %s";
        text.add(String.format(row, "li", "$v0", "1"));
        text.add(String.format(row,"lw", "$a0", "("+temp+")"));
        text.add("syscall");
    }

    void generateBranch(String label) {
        text.add("b "+label);
    }

    void generateGreaterThan(String arg1, String arg2, String label) {
        String row = "%s %s, %s, %s";
        text.add(String.format(row, "bgt",arg1, arg2, label));
    }

    void generateLessThan(String Arg1, String Arg2, String label) {
        String row = "%s %s, %s, %s";
        text.add(String.format(row, "blt",Arg1, Arg2, label));
    }

    void generateGreaterEqual(String Arg1, String Arg2, String label) {
        String row = "%s %s, %s, %s";
        text.add(String.format(row, "bge",Arg1, Arg2, label));
    }

    void generateLessEqual(String Arg1, String Arg2, String label) {
        String row = "%s %s, %s, %s";
        text.add(String.format(row, "ble",Arg1, Arg2, label));
    }

    void generateEqual(String Arg1, String Arg2, String label) {
        String row = "%s %s, %s, %s";
        text.add(String.format(row, "beq",Arg1, Arg2, label));
    }

    void generateJal(String arg1) {
        String row = "%s %s";
        text.add(String.format(row, "jal", arg1));
    }

    void generateParam(String arg1, int paramCount) {
        String row = "%s %s, %s";
        text.add(String.format(row, "move", "$a"+paramCount, arg1));
    }
}
