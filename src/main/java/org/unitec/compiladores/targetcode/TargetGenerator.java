/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unitec.compiladores.targetcode;

import java.util.HashMap;
import java.util.Map;
import org.unitec.compiladores.Simbolo;
import org.unitec.compiladores.TablaSimbolos;
import org.unitec.compiladores.intermediatecode.Cuadruplo;
import org.unitec.compiladores.intermediatecode.TablaCuadruplos;

/**
 *
 * @author Luis Lainez
 */
public class TargetGenerator {

    TablaSimbolos ts = new TablaSimbolos();
    TablaCuadruplos tc = new TablaCuadruplos();
    CodigoFinalTabla cft = new CodigoFinalTabla();
    HashMap<Integer, String> labelControl = new HashMap();
    HashMap<String, Boolean> tempVivos = new HashMap();
    HashMap<String, String> tempEquivalente = new HashMap();
    int labelCount = 0;
    int paramCount = 0;

    public TargetGenerator(TablaSimbolos TS, TablaCuadruplos TC) throws Exception {
        ts = TS;
        tc = TC;
        for (int i = 0; i <= 9; i++) {
            String temp = "$t" + i;
            tempVivos.put(temp, false);
        }
    }

    public void generateFinalCode() throws Exception {
        generateVariables();
        lookForLabels();
        recorrerCuadruplos();
        cft.generateFile("MIPSFinalCode.s");
    }

    public void generateVariables() {
        for (Simbolo S : ts.Simbolos) {
            if (S.getAmbito().equals("main") && S.isVariable()) {
                String tipo = S.getTipo();
                switch (tipo) {
                    case "boolean":
                    case "integer": {
                        cft.addIntegerDataVariable(S.getId());
                        break;
                    }
                    case "char": {
                        cft.addCharDataVariable(S.getId());
                        break;
                    }
                    case "string": {
                        // T_T Esta malo
                        cft.addIntegerDataVariable(S.getId());
                        break;
                    }
                }
                if (tipo.startsWith("Array")) {
                    String[] list = tipo.split("\\.");
                    int space = Integer.parseInt(list[3]) - Integer.parseInt(list[2]) + 1;
                    if (list[1].equals("boolean") || list[1].equals("integer")) {
                        space *= 4;
                    }
                    cft.addArrayDataVariable(S.getId(), space);
                }
            }
        }
    }

    public void recorrerCuadruplos() throws Exception {
        String ambitoActual = "";
        for (int i = 0; i < tc.getSize(); i++) {
            if (labelControl.containsKey(i)) {
                cft.addLabel(labelControl.get(i));
            }
            Cuadruplo C = tc.item(i);
            String op = C.getOperacion();
            String arg1 = C.getArg1();
            String arg2 = C.getArg2();
            String resultado = C.getResultado();
            switch (op) {
                case "WRITE": {
                    generateWrite(C, ambitoActual);
                    break;
                }
                case "LABEL": {
                    ambitoActual = C.getArg1();
                    cft.addLabel(C.getArg1());
                    if (ambitoActual.equals("main")) {
                        cft.generateMove("$fp", "$sp");
                    }else{
                        //funciones
                    }
                    break;
                }
                case "READ": {
                    this.generateRead(C, ambitoActual);
                    break;
                }
                case ":=": {
                    if (arg1.contains("$t")) {
                        String tempArg1 = this.getTemp(arg1);
                        if (resultado.contains("$t")) {
                            String tempResultado = this.getTempClean(resultado);
                            cft.generateMove(tempArg1, tempResultado);
                        } else {
                            cft.generateAssignTemp(resultado, tempArg1);
                        }
                    } else if (arg1.contains("'")) {
                        //
                    } else if (arg1.matches("[0-9]+")) {
                        String temp = this.getTempDisponible(resultado);
                        cft.generateAssignNum(arg1, temp);
                    } else if (arg1.equals("true") || arg1.equals("false")) {
                        String temp = this.getTempDisponible(resultado);
                        if (arg1.equals("true")) {
                            cft.generateAssignNum("1", temp);
                        } else {
                            cft.generateAssignNum("0", temp);
                        }
                    } else if(arg1.equals("RET")){
                        cft.generateMove("$v0", "_"+resultado);
                    } else {
                        String temp = this.getTempDisponible(resultado);
                        cft.generateAssignVarLoad(arg1, temp);
                    }
                    break;
                }
                case "=[]": {
                    String tempAvailable = this.getTempDisponible(resultado);
                    String address = this.getTempClean(arg1);
                    String Arg2 = this.getTempClean(arg2);
                    cft.generateReadArray(arg1, Arg2, address, tempAvailable);
                    break;
                }
                case "[]=": {
                    String arrayAdress = this.getTempDisponible(resultado);
                    String indice = this.getTempClean(arg1);
                    String valor = this.getTempClean(arg2);
                    cft.generateWriteArray(indice, valor, resultado, arrayAdress);
                    break;
                }
                case "+": {
                    String tempAvailable = this.getTempDisponible(resultado);
                    String Arg1 = this.getTempClean(arg1);
                    String Arg2 = this.getTempClean(arg2);
                    cft.generateAdd(Arg1, Arg2, tempAvailable);
                    if (!resultado.contains("$t")) {
                        cft.generateAssignTemp(resultado,tempAvailable);
                    }
                    break;
                }
                case "-": {
                    String tempAvailable = this.getTempDisponible(resultado);
                    String Arg1 = this.getTempClean(arg1);
                    String Arg2 = this.getTempClean(arg2);
                    cft.generateSub(Arg1, Arg2, tempAvailable);
                    break;
                }
                case "*": {
                    String Arg1 = this.getTempClean(arg1);
                    String Arg2 = this.getTempClean(arg2);
                    cft.generateMult(Arg1, Arg2);
                    if (resultado.contains("$t")) {
                        String ArgResultado = this.getTempDisponible(resultado);
                        cft.generateMFLo(ArgResultado);
                    } else {
                        String temp = this.getTempDisponible(resultado);
                        cft.generateMFLo(temp);
                        cft.generateAssignTemp(resultado, temp);
                        this.getTempClean(resultado);
                    }
                    break;
                }
                case "/": {
                    String Arg1 = this.getTempClean(arg1);
                    String Arg2 = this.getTempClean(arg2);
                    cft.generateDiv(Arg1, Arg2);
                    if (resultado.contains("$t")) {
                        String ArgResultado = this.getTempDisponible(resultado);
                        cft.generateMFLo(ArgResultado);
                    } else {
                        String temp = this.getTempDisponible(resultado);
                        cft.generateMFLo(temp);
                        cft.generateAssignTemp(resultado, temp);
                        this.getTempClean(resultado);
                    }
                    break;
                }
                case "GOTO": {
                    String label = labelControl.get(Integer.parseInt(arg1));
                    cft.generateBranch(label);
                    break;
                }
                case "if>":{
                    String label = labelControl.get(Integer.parseInt(resultado));
                    String Arg1 = this.getTempClean(arg1);
                    String Arg2 = this.getTempClean(arg2);
                    cft.generateGreaterThan(Arg1, Arg2, label);
                    break;
                }
                case "if<":{
                    String label = labelControl.get(Integer.parseInt(resultado));
                    String Arg1 = this.getTempClean(arg1);
                    String Arg2 = this.getTempClean(arg2);
                    cft.generateLessThan(Arg1, Arg2, label);
                    break;
                }
                case "if>=":{
                    String label = labelControl.get(Integer.parseInt(resultado));
                    String Arg1 = this.getTempClean(arg1);
                    String Arg2 = this.getTempClean(arg2);
                    cft.generateGreaterEqual(Arg1, Arg2, label);
                    break;
                }
                case "if<=":{
                    String label = labelControl.get(Integer.parseInt(resultado));
                    String Arg1 = this.getTempClean(arg1);
                    String Arg2 = this.getTempClean(arg2);
                    cft.generateLessEqual(Arg1, Arg2, label);
                    break;
                }
                case "if=":{
                    String label = labelControl.get(Integer.parseInt(resultado));
                    String Arg1 = this.getTempClean(arg1);
                    String Arg2 = this.getTempClean(arg2);
                    cft.generateEqual(Arg1, Arg2, label);
                    break;
                }
                case "CALL":{
                    cft.generateJal(arg1);
                    paramCount = 0;
                    break;
                }
                case "PARAM":{
                    cft.generateParam(arg1,paramCount);
                    paramCount++;
                    break;
                }
            }
            /*if (i==tc.getSize()-1 && op.equals("GOTO")) {
                String label = labelControl.get(Integer.parseInt(arg1));
                cft.addLabel(label);
            }*/
        }
        cft.generateEndOfProgram();

    }

    public void lookForLabels() throws Exception {
        for (int i = 0; i < tc.getSize(); i++) {
            Cuadruplo C = tc.item(i);
            String op = C.getOperacion();
            if (op.equals("GOTO")) {
                String jumpTo = C.getArg1();
                int jump = Integer.parseInt(jumpTo);
                if (!labelControl.containsKey(jump)) {
                    labelControl.put(jump, newLabel());
                }
            } else if (op.startsWith("if")) {
                String jumpTo = C.getResultado();
                int jump = Integer.parseInt(jumpTo);
                if (!labelControl.containsKey(jump)) {
                    labelControl.put(jump, newLabel());
                }
            }
        }
    }

    public void generateWrite(Cuadruplo C, String ambitoActual) throws Exception {
        String param = C.getArg1();
        if (param.startsWith("$t")) {
            String temp = this.getTempClean(param);
            cft.addWriteTemp(C, temp);
        }else if (param.contains("'")) {
            String messageName = cft.addDataMessage(param);
            cft.addWriteStatementLiteral(messageName);
        } else {
            Simbolo S = ts.getVariable(param, ambitoActual);
            if (S == null && !ambitoActual.equals("main")) {
                S = ts.getVariable(param, "main");
            }
            String tipo = S.getTipo();
            if (tipo.equals("string") || tipo.equals("char")) {
                cft.addWriteStringStatement(param);
            } else {
                cft.addWriteStatement(param);
            }
        }
    }

    public void generateRead(Cuadruplo C, String ambitoActual) throws Exception {
        String variable = C.getResultado();

        Simbolo S = ts.getVariable(variable, ambitoActual);
        if (S == null && !ambitoActual.equals("main")) {
            S = ts.getVariable(variable, "main");
        }
        String tipo = S.getTipo();
        if (tipo.equals("string") || tipo.equals("char")) {
            //cft.addWriteStringStatement(variable);
        } else {
            cft.addReadStatement(variable);
        }

    }

    public void printTargetCode() {
        cft.print();
    }

    public String newLabel() {
        return "L" + ++labelCount;
    }

    private String getTempDisponible(String previousTemp) {
        for (Map.Entry<String, Boolean> entry : tempVivos.entrySet()) {
            String key = entry.getKey();
            boolean estaVivo = entry.getValue();
            if (!estaVivo) {
                tempVivos.put(key, true);
                this.tempEquivalente.put(previousTemp, key);
                return key;
            }
        }
        return "";
    }

    private String getTempClean(String arg1) {
        String temp = tempEquivalente.get(arg1);
        tempEquivalente.remove(arg1);
        this.tempVivos.put(temp, false);
        return temp;
    }

    private String getTemp(String arg1) {
        return tempEquivalente.get(arg1);
    }
}
