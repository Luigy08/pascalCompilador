/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unitec.compiladores.intermediatecode;

import java.util.ArrayList;
import org.unitec.compiladores.Simbolo;
import org.unitec.compiladores.TablaSimbolos;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Cesar Bonilla
 */
public class QuadGenerator {

    TablaCuadruplos Cuadruplos = new TablaCuadruplos();
    TablaSimbolos TS = new TablaSimbolos();
    String opracionActual = "";
    String ambitoActual = "main";
    int tempCounter = 0;
    boolean debug = false;

    public QuadGenerator(TablaSimbolos TS) {
        this.TS = TS;
    }

    public TablaCuadruplos getTablaCuadruplos() {
        return this.Cuadruplos;
    }

    public void recorrer(Element rootNode) throws Exception {
        NodeList hijos = rootNode.getChildNodes();
        for (int i = 0; i < hijos.getLength(); i++) {
            Element nodo = (Element) hijos.item(i);
            String nodeName = nodo.getNodeName();
            if (debug) {
                System.out.println("RecorrerArbol - " + nodeName);
            }
            switch (nodeName) {

                case "ProcedureDeclaration": {
                    String functionName = nodo.getAttribute("ID");
                    Cuadruplos.GEN_LABEL(functionName);
                    this.recorrer(nodo);
                    Cuadruplos.GEN("RET", "", "", "");
                    break;
                }
                case "FunctionDeclaration": {
                    String functionName = nodo.getAttribute("ID");
                    Cuadruplos.GEN_LABEL(functionName);
                    this.recorrer(nodo);
                    break;
                }
                case "Body": {
                    if (nodo.getParentNode().getNodeName().equals("Block")) {
                        Cuadruplos.GEN_LABEL("main");
                    }
                    recorrer(nodo);
                    break;
                }
                case "FunctionCall": {
                    cuadruploFuncCall(nodo);
                    break;
                }
                case "IfStatement": {
                    cuadruploIf(nodo);
                    break;
                }
                case "WhileStatement": {
                    cuadruploWhile(nodo);
                    break;
                }
                case "RepeatStatement": {
                    cuadruploRepeat(nodo);
                    break;
                }
                case "ForStatement": {
                    cuadruploFor(nodo);
                    break;
                }
                case "ReadStatement": {
                    Element arg = (Element) nodo.getFirstChild();
                    String argName = arg.getNodeName();
                    switch (argName) {
                        case "ID": {
                            String argValue = arg.getAttribute("Value");
                            Cuadruplos.GEN("READ", "", "", argValue);
                            break;
                        }
                        case "ARRAY": {
                            cuadruploArray(arg);
                            String temp = this.getTemp();
                            String argValue = arg.getAttribute("Value");
                            Cuadruplos.GEN("READ[]", temp, "", argValue);
                            break;
                        }
                    }

                    break;
                }
                case "WriteStatement": {
                    NodeList lista = nodo.getChildNodes();
                    Element arg1 = (Element) lista.item(0);
                    String arg1Value = arg1.getAttribute("Value");
                    Element arg2 = null;
                    String arg2Name = "";
                    if (lista.getLength() > 1) {
                        arg2 = (Element) lista.item(1);
                        arg2Name = arg2.getNodeName();
                        String argValue = "";
                        switch (arg2Name) {
                            case "ID": {
                                argValue = arg2.getAttribute("Value");
                                break;
                            }
                            case "ARRAY": {
                                cuadruploArray(arg2);
                                String temp = this.getTemp();
                                argValue = temp;
                                break;
                            }
                        }
                        Cuadruplos.GEN("WRITE", arg1Value, "", "");
                        Cuadruplos.GEN("WRITE", argValue, "", "");
                    } else {
                        Cuadruplos.GEN("WRITE", arg1Value, "", "");
                    }

                    break;
                }
                case "Assignment": {
                    cuadruploAssignment(nodo);
                    break;
                }
                case "AND":
                case "OR":
                case "NOT": {
                    cuadruploRelacional(nodo);
                    break;
                }
                case "GreaterThan":
                case "LessThan":
                case "Equals":
                case "LessOrEqual":
                case "GreaterOrEqual":
                case "Different": {
                    this.cuadruplosExpresion(nodo);
                    Element parent = (Element) nodo.getParentNode();
                    String listaV = nodo.getAttribute("listaV");
                    String listaF = nodo.getAttribute("listaF");
                    parent.setAttribute("listaV", listaV);
                    parent.setAttribute("listaF", listaF);
                    break;
                }
                case "Div":
                case "Minus":
                case "Times":
                case "Plus": {
                    cuadruploAritmetico(nodo);
                    break;
                }
                default: {
                    recorrer(nodo);
                    break;
                }
            }
        }

    }

    public void cuadruploAritmetico(Element nodo) throws Exception {
        String nodeName = nodo.getNodeName();
        if (debug) {
            System.out.println("cuadruploArit: " + nodeName);
        }
        switch (nodeName) {
            case "Div":
            case "Minus":
            case "Times":
            case "Plus": {
                Element arg1 = (Element) nodo.getFirstChild();
                Element arg2 = (Element) nodo.getLastChild();
                String arg1Name = arg1.getNodeName();
                String arg2Name = arg2.getNodeName();
                boolean arg1IsArray = arg1Name.equals("ARRAY");
                boolean arg2IsArray = arg2Name.equals("ARRAY");
                boolean arg1IsFinal = arg1Name.equals("Literal") || arg1Name.equals("ID") || arg1IsArray;
                boolean arg2IsFinal = arg2Name.equals("Literal") || arg2Name.equals("ID") || arg2IsArray;

                if (arg1IsFinal && arg2IsFinal) {
                    if (arg1IsArray && arg2IsArray) {
                        cuadruploAritmetico(arg1);
                        String tempArg1 = this.getTemp();
                        cuadruploAritmetico(arg2);
                        String tempArg2 = this.getTemp();

                        String temp = this.newTemp();
                        String operacion = nodo.getAttribute("Value");
                        if (debug) {
                            System.out.println("Operacion: " + operacion);
                        }
                        Cuadruplos.GEN(operacion, tempArg1, tempArg2, temp);
                    } else if (arg1IsArray) {
                        cuadruploAritmetico(arg1);
                        String tempArg = this.getTemp();
                        String temp = this.newTemp();
                        Cuadruplos.GEN(":=", arg2.getAttribute("Value"), temp);
                        String operacion = nodo.getAttribute("Value");
                        if (debug) {
                            System.out.println("Operacion: " + operacion);
                        }
                        String newTemp = this.newTemp();
                        Cuadruplos.GEN(operacion, tempArg, temp, newTemp);
                    } else if (arg2IsArray) {
                        cuadruploAritmetico(arg2);
                        String tempArg = this.getTemp();
                        String temp = this.newTemp();
                        Cuadruplos.GEN(":=", arg2.getAttribute("Value"), temp);
                        String operacion = nodo.getAttribute("Value");
                        if (debug) {
                            System.out.println("Operacion: " + operacion);
                        }
                        String newTemp = this.newTemp();
                        Cuadruplos.GEN(operacion, temp, tempArg, newTemp);
                    } else {
                        String temp = this.newTemp();
                        String operacion = nodo.getAttribute("Value");
                        if (debug) {
                            System.out.println("Operacion: " + operacion);
                        }
                        Cuadruplos.GEN(":=", arg1.getAttribute("Value"), temp);
                        String firstTemp = this.getTemp();
                        temp = newTemp();
                        Cuadruplos.GEN(":=", arg2.getAttribute("Value"), temp);
                        String secondTemp = this.getTemp();
                        temp = newTemp();
                        Cuadruplos.GEN(operacion, firstTemp, secondTemp, temp);
                    }
                } else if (arg1IsFinal) {
                    if (arg1IsArray) {
                        cuadruploAritmetico(arg1);
                        String tempArg1 = this.getTemp();
                        cuadruploAritmetico(arg2);
                        String tempArg2 = this.getTemp();

                        String temp = this.newTemp();
                        String operacion = nodo.getAttribute("Value");
                        if (debug) {
                            System.out.println("Operacion: " + operacion);
                        }
                        Cuadruplos.GEN(operacion, tempArg1, tempArg2, temp);
                    } else {
                        cuadruploAritmetico(arg2);
                        String lastTemp = this.getTemp();
                        String newTemp = this.newTemp();
                        String operacion = nodo.getAttribute("Value");
                        if (debug) {
                            System.out.println("Operacion: " + operacion);
                        }
                        Cuadruplos.GEN(":=", arg1.getAttribute("Value"), newTemp);
                        String temp = this.getTemp();
                        newTemp = this.newTemp();
                        Cuadruplos.GEN(operacion, temp, lastTemp, newTemp);
                    }
                } else if (arg2IsFinal) {
                    if (arg2IsArray) {
                        cuadruploAritmetico(arg1);
                        String tempArg1 = this.getTemp();
                        cuadruploAritmetico(arg2);
                        String tempArg2 = this.getTemp();

                        String temp = this.newTemp();
                        String operacion = nodo.getAttribute("Value");
                        if (debug) {
                            System.out.println("Operacion: " + operacion);
                        }
                        Cuadruplos.GEN(operacion, tempArg1, tempArg2, temp);
                    } else {
                        cuadruploAritmetico(arg1);
                        String lastTemp = this.getTemp();
                        String newTemp = this.newTemp();
                        String operacion = nodo.getAttribute("Value");
                        if (debug) {
                            System.out.println("Operacion: " + operacion);
                        }
                        Cuadruplos.GEN(":=", arg2.getAttribute("Value"), newTemp);
                        String temp = this.getTemp();
                        newTemp = this.newTemp();
                        Cuadruplos.GEN(operacion, lastTemp, temp, newTemp);
                    }
                } else { // Both are OPs
                    cuadruploAritmetico(arg1);
                    String tempArg1 = this.getTemp();
                    cuadruploAritmetico(arg2);
                    String tempArg2 = this.getTemp();

                    String temp = this.newTemp();
                    String operacion = nodo.getAttribute("Value");
                    if (debug) {
                        System.out.println("Operacion: " + operacion);
                    }
                    Cuadruplos.GEN(operacion, tempArg1, tempArg2, temp);
                }
                break;
            }
            case "ARRAY": {
                this.cuadruploArray(nodo);
                break;
            }
            default: {
                break;
            }
        }
    }

    public void cuadruploAssignment(Element nodo) throws Exception {
        Element arg1 = (Element) nodo.getFirstChild();
        Element arg2 = (Element) nodo.getLastChild();

        String temp1 = "";
        String temp2 = "";

        switch (arg2.getNodeName()) {
            case "ID":
            case "Literal": {
                String t = arg2.getAttribute("Value");
                temp2 = this.newTemp();
                this.Cuadruplos.GEN(":=", t, temp2);
                break;
            }
            case "FunctionCall": {
                this.cuadruploFuncCall(arg2);
                temp2 = "RET";
                break;
            }
            case "AND":
            case "OR":
            case "NOT":
            case "GreaterThan":
            case "LessThan":
            case "Equals":
            case "LessOrEqual":
            case "GreaterOrEqual":
            case "Different": {
                cuadruploRelacional(arg2);
                String newTemp = this.newTemp();
                int M1 = Cuadruplos.getSize();
                Cuadruplos.GEN(":=", "1", "", newTemp);
                Cuadruplos.GEN_GOTO(Cuadruplos.getSize() + 2 + "");
                int M2 = Cuadruplos.getSize();
                Cuadruplos.GEN(":=", "0", "", newTemp);
                this.completa(M1, arg2.getAttribute("listaV"));
                this.completa(M2, arg2.getAttribute("listaF"));
                temp2 = this.getTemp();
                break;
            }
            default: {
                cuadruploAritmetico(arg2);
                temp2 = this.getTemp();
                break;
            }
        }

        if (arg1.getNodeName().equals("ID")) {
            String isReturn = nodo.getAttribute("Return");
            if (isReturn.isEmpty()) {
                temp1 = arg1.getAttribute("Value");
                Cuadruplos.GEN(":=", temp2, temp1);
            } else {
                Cuadruplos.GEN("FRET", temp2, "", "");
            }

        } else if (arg1.getNodeName().equals("ARRAY")) {
            Element arg = (Element) arg1.getFirstChild();
            String argName = arg.getNodeName();
            String Valex = arg1.getAttribute("Value");
            Simbolo S = TS.getVariable(Valex, ambitoActual);
            String tipo = S.getTipo();
            String indiceInicial = tipo.split("\\.")[2];
            if (argName.equals("ID") || argName.equals("Literal")) {
                String tempArg1 = this.newTemp();
                Cuadruplos.GEN(":=", arg.getAttribute("Value"), tempArg1);
                String tempArg2 = this.newTemp();
                Cuadruplos.GEN(":=", indiceInicial, tempArg2);
                String newTemp = this.newTemp();
                Cuadruplos.GEN("-", tempArg1, tempArg2, newTemp);
                String temp = this.getTemp();
                tempArg2 = this.newTemp();
                Cuadruplos.GEN(":=", this.getTypeSize(tipo), tempArg2);
                newTemp = this.newTemp();
                Cuadruplos.GEN("*", temp, tempArg2, newTemp);
                temp = this.getTemp();
                Cuadruplos.GEN("[]=", temp, temp2, Valex);
            } else {
                cuadruploAritmetico(arg);
                String temp = this.getTemp();
                String tempArg2 = this.newTemp();
                Cuadruplos.GEN(":=", indiceInicial, tempArg2);
                String newTemp = this.newTemp();
                Cuadruplos.GEN("-", temp, tempArg2, newTemp);
                temp = this.getTemp();
                tempArg2 = this.newTemp();
                Cuadruplos.GEN(":=", this.getTypeSize(tipo), tempArg2);
                newTemp = this.newTemp();
                Cuadruplos.GEN("*", temp, tempArg2, newTemp);
                temp = this.getTemp();
                Cuadruplos.GEN("[]=", temp, temp2, Valex);
            }
        }
    }

    public void cuadruplosExpresion(Element nodo) throws Exception {
        if (debug) {
            System.out.println("cuadruplosExpresion: " + nodo.getNodeName());
        }
        Element arg1 = (Element) nodo.getFirstChild();
        Element arg2 = (Element) nodo.getLastChild();
        Element parent = (Element) nodo.getParentNode();

        String arg1Name = arg1.getNodeName();
        String arg2Name = arg2.getNodeName();

        String t1 = "";
        String t2 = "";
        String tResultado = "";
        String op = nodo.getAttribute("Value");

        if (arg1Name.equals("ID") || arg1Name.equals("Literal")) {
            t1 = this.newTemp();
            Cuadruplos.GEN(":=", arg1.getAttribute("Value"), t1);
        } else if (arg1Name.equals("ARRAY")) {
            cuadruploArray(arg1);
            t1 = this.getTemp();
        } else {
            cuadruploAritmetico(arg1);
            t1 = this.getTemp();
        }

        if (arg2Name.equals("ID") || arg2Name.equals("Literal")) {
            t2 = this.newTemp();
            Cuadruplos.GEN(":=", arg2.getAttribute("Value"), t2);
        } else if (arg2Name.equals("ARRAY")) {
            cuadruploArray(arg2);
            t2 = this.getTemp();
        } else {
            cuadruploAritmetico(arg2);
            t2 = this.getTemp();
        }

        nodo.setAttribute("listaV", this.crearLista(Cuadruplos.getSize()));
        nodo.setAttribute("listaF", this.crearLista(Cuadruplos.getSize() + 1));

        Cuadruplos.GEN("if" + op, t1, t2, "@");
        Cuadruplos.GEN_GOTO("@");
    }

    public void cuadruploArray(Element nodo) throws Exception {
        Element arg = (Element) nodo.getFirstChild();
        String argName = arg.getNodeName();
        String operacion = "=[]";
        String IDArray = nodo.getAttribute("Value");
        Simbolo S = TS.getVariable(IDArray, ambitoActual);
        String tipo = S.getTipo();
        String indiceInicial = tipo.split("\\.")[2];
        if (argName.equals("ID") || argName.equals("Literal")) {
            String temp1 = this.newTemp();
            Cuadruplos.GEN(":=", arg.getAttribute("Value"), temp1);
            String temp2 = this.newTemp();
            Cuadruplos.GEN(":=", indiceInicial, temp2);
            String newTemp = this.newTemp();
            Cuadruplos.GEN("-", temp1, temp2, newTemp);

            temp1 = this.getTemp();
            temp2 = this.newTemp();
            Cuadruplos.GEN(":=", getTypeSize(tipo.split("\\.")[1]), temp2);
            newTemp = this.newTemp();
            Cuadruplos.GEN("*", temp1, temp2, newTemp);
            temp2 = this.getTemp();
            newTemp = this.newTemp();
            Cuadruplos.GEN(operacion, IDArray, temp2, newTemp);
        } else {
            cuadruploAritmetico(arg);
            String temp = this.getTemp();
            String temp2 = this.newTemp();
            Cuadruplos.GEN(":=", indiceInicial, temp2);
            String newTemp = this.newTemp();
            Cuadruplos.GEN("-", temp, temp2, newTemp);
            temp = this.getTemp();
            temp2 = this.newTemp();
            Cuadruplos.GEN(":=", getTypeSize(tipo.split("\\.")[1]), temp2);
            newTemp = this.newTemp();
            Cuadruplos.GEN("*", temp, temp2, newTemp);
            temp = this.getTemp();
            newTemp = this.newTemp();
            Cuadruplos.GEN(operacion, IDArray, temp, newTemp);
        }
    }

    private void cuadruploAnd(Element nodo) throws Exception {
        if (debug) {
            System.out.println("cuadruploAND: " + nodo.getNodeName());
        }
        Element arg1 = (Element) nodo.getFirstChild();
        Element arg2 = (Element) nodo.getLastChild();

        String arg1Name = arg1.getNodeName();
        String arg2Name = arg2.getNodeName();

        switch (arg1Name) {
            case "ID": {
                String arg1Value = arg1.getAttribute("Value");
                arg1.setAttribute("listaV", crearLista(Cuadruplos.getSize()));
                arg1.setAttribute("listaF", crearLista(Cuadruplos.getSize() + 1));
                Cuadruplos.GEN("if=", arg1Value, "1", "@");
                Cuadruplos.GEN_GOTO("@");
                break;
            }
            case "Literal": {
                String arg1Value = arg1.getAttribute("Value");
                arg1.setAttribute("listaV", crearLista(Cuadruplos.getSize()));
                arg1.setAttribute("listaF", crearLista(Cuadruplos.getSize() + 1));
                Cuadruplos.GEN("if=", arg1Value, "1", "@");
                Cuadruplos.GEN_GOTO("@");
                break;
            }
            case "ARRAY": {
                cuadruploArray(arg1);
                String temp = this.getTemp();
                arg1.setAttribute("listaV", crearLista(Cuadruplos.getSize()));
                arg1.setAttribute("listaF", crearLista(Cuadruplos.getSize() + 1));
                Cuadruplos.GEN("if=", temp, "1", "@");
                Cuadruplos.GEN_GOTO("@");
                break;
            }
            case "GreaterThan":
            case "LessThan":
            case "Equals":
            case "LessOrEqual":
            case "GreaterOrEqual":
            case "Different": {
                cuadruplosExpresion(arg1);
                break;
            }
            default: {
                cuadruploRelacional(arg1);
                break;
            }

        }

        int M1 = Cuadruplos.getSize();

        switch (arg2Name) {
            case "ID": {
                String arg2Value = arg2.getAttribute("Value");
                arg2.setAttribute("listaV", crearLista(Cuadruplos.getSize()));
                arg2.setAttribute("listaF", crearLista(Cuadruplos.getSize() + 1));
                Cuadruplos.GEN("if=", arg2Value, "1", "@");
                Cuadruplos.GEN_GOTO("@");
                break;
            }
            case "Literal": {
                String arg2Value = arg2.getAttribute("Value");
                arg2.setAttribute("listaV", crearLista(Cuadruplos.getSize()));
                arg2.setAttribute("listaF", crearLista(Cuadruplos.getSize() + 1));
                Cuadruplos.GEN("if=", arg2Value, "1", "@");
                Cuadruplos.GEN_GOTO("@");
                break;
            }
            case "ARRAY": {
                cuadruploArray(arg2);
                String temp = this.getTemp();
                arg2.setAttribute("listaV", crearLista(Cuadruplos.getSize()));
                arg2.setAttribute("listaF", crearLista(Cuadruplos.getSize() + 1));
                Cuadruplos.GEN("if=", temp, "1", "@");
                Cuadruplos.GEN_GOTO("@");
                break;
            }
            case "GreaterThan":
            case "LessThan":
            case "Equals":
            case "LessOrEqual":
            case "GreaterOrEqual":
            case "Different": {
                cuadruplosExpresion(arg2);
                break;
            }
            default: {
                cuadruploRelacional(arg2);
                break;
            }

        }

        this.completa(M1, arg1.getAttribute("listaV"));
        String listaFusionada = this.fusiona(arg1.getAttribute("listaF"), arg2.getAttribute("listaF"));
        nodo.setAttribute("listaF", listaFusionada);
        nodo.setAttribute("listaV", arg2.getAttribute("listaV"));
        
    }

    private void cuadruploOr(Element nodo) throws Exception {
        if (debug) {
            System.out.println("cuadruploOR: " + nodo.getNodeName());
        }
        Element arg1 = (Element) nodo.getFirstChild();
        Element arg2 = (Element) nodo.getLastChild();

        String arg1Name = arg1.getNodeName();
        String arg2Name = arg2.getNodeName();

        switch (arg1Name) {
            case "ID": {
                String arg1Value = arg1.getAttribute("Value");
                arg1.setAttribute("listaV", crearLista(Cuadruplos.getSize()));
                arg1.setAttribute("listaF", crearLista(Cuadruplos.getSize() + 1));
                Cuadruplos.GEN("if=", arg1Value, "1", "@");
                Cuadruplos.GEN_GOTO("@");
                break;
            }
            case "Literal": {
                String arg1Value = arg1.getAttribute("Value");
                arg1.setAttribute("listaV", crearLista(Cuadruplos.getSize()));
                arg1.setAttribute("listaF", crearLista(Cuadruplos.getSize() + 1));
                Cuadruplos.GEN("if=", arg1Value, "1", "@");
                Cuadruplos.GEN_GOTO("@");
                break;
            }
            case "ARRAY": {
                cuadruploArray(arg1);
                String temp = this.getTemp();
                arg1.setAttribute("listaV", crearLista(Cuadruplos.getSize()));
                arg1.setAttribute("listaF", crearLista(Cuadruplos.getSize() + 1));
                Cuadruplos.GEN("if=", temp, "1", "@");
                Cuadruplos.GEN_GOTO("@");
                break;
            }
            case "GreaterThan":
            case "LessThan":
            case "Equals":
            case "LessOrEqual":
            case "GreaterOrEqual":
            case "Different": {
                cuadruplosExpresion(arg1);
                break;
            }
            default: {
                cuadruploRelacional(arg1);
                break;
            }

        }
        int M1 = Cuadruplos.getSize();

        switch (arg2Name) {
            case "ID": {
                String arg2Value = arg2.getAttribute("Value");
                arg2.setAttribute("listaV", crearLista(Cuadruplos.getSize()));
                arg2.setAttribute("listaF", crearLista(Cuadruplos.getSize() + 1));
                Cuadruplos.GEN("if=", arg2Value, "1", "@");
                Cuadruplos.GEN_GOTO("@");
                break;
            }
            case "Literal": {
                String arg2Value = arg2.getAttribute("Value");
                arg2.setAttribute("listaV", crearLista(Cuadruplos.getSize()));
                arg2.setAttribute("listaF", crearLista(Cuadruplos.getSize() + 1));
                Cuadruplos.GEN("if=", arg2Value, "1", "@");
                Cuadruplos.GEN_GOTO("@");
                break;
            }
            case "ARRAY": {
                cuadruploArray(arg2);
                String temp = this.getTemp();
                arg2.setAttribute("listaV", crearLista(Cuadruplos.getSize()));
                arg2.setAttribute("listaF", crearLista(Cuadruplos.getSize() + 1));
                Cuadruplos.GEN("if=", temp, "1", "@");
                Cuadruplos.GEN_GOTO("@");
                break;
            }
            case "GreaterThan":
            case "LessThan":
            case "Equals":
            case "LessOrEqual":
            case "GreaterOrEqual":
            case "Different": {
                cuadruplosExpresion(arg2);
                break;
            }
            default: {
                cuadruploRelacional(arg2);
                break;
            }
        }

        this.completa(M1, arg1.getAttribute("listaF"));
        String listaFusionada = this.fusiona(arg1.getAttribute("listaV"), arg2.getAttribute("listaV"));
        nodo.setAttribute("listaV", listaFusionada);
        nodo.setAttribute("listaF", arg2.getAttribute("listaF"));
    }

    private void cuadruploNot(Element nodo) throws Exception {
        if (debug) {
            System.out.println("cuadruploNOT: " + nodo.getNodeName());
        }
        Element arg1Node = (Element) nodo.getFirstChild();
        String arg1Name = arg1Node.getNodeName();

        switch (arg1Name) {
            case "ID": {
                String arg1Value = arg1Node.getAttribute("Value");
                arg1Node.setAttribute("listaV", crearLista(Cuadruplos.getSize()));
                arg1Node.setAttribute("listaF", crearLista(Cuadruplos.getSize() + 1));
                Cuadruplos.GEN("if=", arg1Value, "1", "@");
                Cuadruplos.GEN_GOTO("@");
                break;
            }
            case "Literal": {
                String arg1Value = arg1Node.getAttribute("Value");
                arg1Node.setAttribute("listaV", crearLista(Cuadruplos.getSize()));
                arg1Node.setAttribute("listaF", crearLista(Cuadruplos.getSize() + 1));
                Cuadruplos.GEN("if=", arg1Value, "1", "@");
                Cuadruplos.GEN_GOTO("@");
                break;
            }
            case "ARRAY": {
                cuadruploArray(arg1Node);
                String temp = this.getTemp();
                arg1Node.setAttribute("listaV", crearLista(Cuadruplos.getSize()));
                arg1Node.setAttribute("listaF", crearLista(Cuadruplos.getSize() + 1));
                Cuadruplos.GEN("if=", temp, "1", "@");
                Cuadruplos.GEN_GOTO("@");
                break;
            }
            case "GreaterThan":
            case "LessThan":
            case "Equals":
            case "LessOrEqual":
            case "GreaterOrEqual":
            case "Different": {
                cuadruplosExpresion(arg1Node);
                break;
            }
            default: {
                cuadruploRelacional(arg1Node);
                break;
            }

        }
        nodo.setAttribute("listaV", arg1Node.getAttribute("listaF"));
        nodo.setAttribute("listaF", arg1Node.getAttribute("listaV"));
    }

    private void cuadruploRelacional(Element node) throws Exception {
        String nodeName = node.getNodeName();
        if (debug) {
            System.out.println("cuadruploRelacional: " + node.getNodeName());
        }
        switch (nodeName) {
            case "AND": {
                cuadruploAnd(node);
                break;
            }
            case "OR": {
                cuadruploOr(node);
                break;
            }
            case "NOT": {
                cuadruploNot(node);
                break;
            }
            case "GreaterThan":
            case "LessThan":
            case "Equals":
            case "LessOrEqual":
            case "GreaterOrEqual":
            case "Different": {
                cuadruplosExpresion(node);
                break;
            }

        }
    }

    private void cuadruploIf(Element nodo) throws Exception {
        if (debug) {
            System.out.println("cuadruploIf: " + nodo.getNodeName());
        }
        NodeList lista = nodo.getChildNodes();
        Element expression = (Element) lista.item(0);
        Element body = (Element) lista.item(1);
        Element elseIf = null;
        String elseIfName = "";
        if (lista.getLength() > 2) {
            elseIf = (Element) lista.item(2);
            elseIfName = elseIf.getNodeName();
        }
        if (expression.getNodeName().equals("Literal") || expression.getNodeName().equals("ID")) {
            String newTemp = this.newTemp();
            Cuadruplos.GEN(":=", expression.getAttribute("Value"), newTemp);
            String temp = this.newTemp();
            Cuadruplos.GEN(":=", "1", temp);
            expression.setAttribute("listaV", this.crearLista(Cuadruplos.getSize()));
            expression.setAttribute("listaF", this.crearLista(Cuadruplos.getSize() + 1));
            
            Cuadruplos.GEN("if=", newTemp, temp, "@");
            Cuadruplos.GEN_GOTO("@");
        } else {
            this.cuadruploRelacional(expression);
        }

        int M1 = Cuadruplos.getSize();

        recorrer(body);

        int N1 = Cuadruplos.GEN_GOTO("@");
        nodo.setAttribute("listaF", crearLista(N1));
        int M2 = Cuadruplos.getSize();

        this.completa(M1, expression.getAttribute("listaV"));
        this.completa(M2, expression.getAttribute("listaF"));
        switch (elseIfName) {
            case "IfStatement": {
                cuadruploIf(elseIf);
                String listaF = elseIf.getAttribute("listaF");
                nodo.setAttribute("listaF", fusiona(listaF, nodo.getAttribute("listaF")));
                break;
            }
            case "Body": {
                recorrer(elseIf);
                int M3 = Cuadruplos.GEN_GOTO("@");
                String listaF = nodo.getAttribute("listaF");
                nodo.setAttribute("listaF", fusiona(listaF, this.crearLista(M3)));
                break;
            }
        }

        int endOfIf = Cuadruplos.getSize();
        this.completa(endOfIf, nodo.getAttribute("listaF"));
    }

    private void cuadruploWhile(Element nodo) throws Exception {
        if (debug) {
            System.out.println("cuadruploWhile: " + nodo.getNodeName());
        }
        Element expression = (Element) nodo.getFirstChild();
        Element body = (Element) nodo.getLastChild();

        String nodeName = expression.getNodeName();

        int M1 = Cuadruplos.getSize();
        switch (nodeName) {
            case "ID": {
                String arg1Value = expression.getAttribute("Value");
                expression.setAttribute("listaV", crearLista(Cuadruplos.getSize()));
                expression.setAttribute("listaF", crearLista(Cuadruplos.getSize() + 1));
                Cuadruplos.GEN("if=", arg1Value, "1", "@");
                Cuadruplos.GEN_GOTO("@");
                break;
            }
            case "Literal": {
                String arg1Value = expression.getAttribute("Value");
                expression.setAttribute("listaV", crearLista(Cuadruplos.getSize()));
                expression.setAttribute("listaF", crearLista(Cuadruplos.getSize() + 1));
                Cuadruplos.GEN("if=", arg1Value, "1", "@");
                Cuadruplos.GEN_GOTO("@");
                break;
            }
            case "ARRAY": {
                cuadruploArray(expression);
                String temp = this.getTemp();
                expression.setAttribute("listaV", crearLista(Cuadruplos.getSize()));
                expression.setAttribute("listaF", crearLista(Cuadruplos.getSize() + 1));
                Cuadruplos.GEN("if=", temp, "1", "@");
                Cuadruplos.GEN_GOTO("@");
                break;
            }
            default: {
                this.cuadruploRelacional(expression);
                break;
            }
        }

        int M2 = Cuadruplos.getSize();
        recorrer(body);

        this.completa(M2, expression.getAttribute("listaV"));
        Cuadruplos.GEN_GOTO(M1 + "");
        int M3 = Cuadruplos.getSize();
        this.completa(M3, expression.getAttribute("listaF"));

    }

    private void cuadruploRepeat(Element nodo) throws Exception {
        if (debug) {
            System.out.println("cuadruploRepeat: " + nodo.getNodeName());
        }
        Element body = (Element) nodo.getFirstChild();
        Element expression = (Element) nodo.getLastChild();
        String nodeName = expression.getNodeName();

        int M1 = Cuadruplos.getSize();
        recorrer(body);

        switch (nodeName) {
            case "ID": {
                String arg1Value = expression.getAttribute("Value");
                expression.setAttribute("listaV", crearLista(Cuadruplos.getSize()));
                expression.setAttribute("listaF", crearLista(Cuadruplos.getSize() + 1));
                Cuadruplos.GEN("if=", arg1Value, "1", "@");
                Cuadruplos.GEN_GOTO("@");
                break;
            }
            case "Literal": {
                String arg1Value = expression.getAttribute("Value");
                expression.setAttribute("listaV", crearLista(Cuadruplos.getSize()));
                expression.setAttribute("listaF", crearLista(Cuadruplos.getSize() + 1));
                Cuadruplos.GEN("if=", arg1Value, "1", "@");
                Cuadruplos.GEN_GOTO("@");
                break;
            }
            case "ARRAY": {
                cuadruploArray(expression);
                String temp = this.getTemp();
                expression.setAttribute("listaV", crearLista(Cuadruplos.getSize()));
                expression.setAttribute("listaF", crearLista(Cuadruplos.getSize() + 1));
                Cuadruplos.GEN("if=", temp, "1", "@");
                Cuadruplos.GEN_GOTO("@");
                break;
            }
            default: {
                this.cuadruploRelacional(expression);
                break;
            }
        }

        this.completa(M1, expression.getAttribute("listaF"));
        int M2 = Cuadruplos.getSize();
        this.completa(M2, expression.getAttribute("listaV"));
    }

    private void cuadruploFor(Element nodo) throws Exception {
        NodeList lista = nodo.getChildNodes();
        Element assignment = (Element) lista.item(0);
        Element end = (Element) lista.item(1);
        Element body = (Element) lista.item(2);

        this.cuadruploAssignment(assignment);
        Cuadruplo QUAD = Cuadruplos.item(Cuadruplos.getSize() - 1);
        String iterator = QUAD.resultado;

        int M1 = Cuadruplos.getSize();

        String endName = end.getNodeName();
        switch (endName) {
            case "Literal":
            case "ID": {
                String temp1 = this.newTemp();
                Cuadruplos.GEN(":=", iterator, temp1);
                String idValex = end.getAttribute("Value");
                String temp2 = this.newTemp();
                Cuadruplos.GEN(":=", idValex, temp2);
                
                String listaV = this.crearLista(Cuadruplos.getSize());
                String listaF = this.crearLista(Cuadruplos.getSize() + 1);
                nodo.setAttribute("listaV", listaV);
                nodo.setAttribute("listaF", listaF);
                
                Cuadruplos.GEN("if<=", temp1, temp2, "@");
                System.out.println("");
                Cuadruplos.GEN_GOTO("@");
                break;
            }
            case "ARRAY": {
                this.cuadruploArray(end);
                String temp = this.getTemp();
                String temp1 = this.newTemp();
                Cuadruplos.GEN(":=", iterator, temp1);
                
                String listaV = this.crearLista(Cuadruplos.getSize());
                String listaF = this.crearLista(Cuadruplos.getSize() + 1);
                nodo.setAttribute("listaV", listaV);
                nodo.setAttribute("listaF", listaF);
                
                Cuadruplos.GEN("if<=", temp1, temp, "@");
                Cuadruplos.GEN_GOTO("@");
                break;
            }
            default: {
                this.cuadruploAritmetico(end);
                String temp = this.getTemp();
                String temp1 = this.newTemp();
                Cuadruplos.GEN(":=", iterator, temp1);
                
                String listaV = this.crearLista(Cuadruplos.getSize());
                String listaF = this.crearLista(Cuadruplos.getSize() + 1);
                nodo.setAttribute("listaV", listaV);
                nodo.setAttribute("listaF", listaF);
                
                Cuadruplos.GEN("if<=", temp1, temp, "@");
                Cuadruplos.GEN_GOTO("@");
                break;
            }
        }
        int M2 = Cuadruplos.getSize();
        recorrer(body);
        String temp1 = this.newTemp();
        Cuadruplos.GEN(":=", iterator, temp1);
        String temp2 = this.newTemp();
        Cuadruplos.GEN(":=", "1", temp2);
        
        Cuadruplos.GEN("+", temp1, temp2, iterator);
        Cuadruplos.GEN_GOTO(M1 + "");
        int M3 = Cuadruplos.getSize();

        this.completa(M2, nodo.getAttribute("listaV"));
        this.completa(M3, nodo.getAttribute("listaF"));

    }

    private void cuadruploFuncCall(Element functionNode) throws Exception {
        Element funcId = (Element) functionNode.getFirstChild();
        Element funcArgsNode = (Element) functionNode.getLastChild();
        if (funcArgsNode.getNodeName().equals("Arguments")) {
            NodeList funcArgs = funcArgsNode.getChildNodes();
            ArrayList<String> parameters = new ArrayList();
            for (int i = 0; i < funcArgs.getLength(); i++) {
                Element currentNode = (Element) funcArgs.item(i);
                String argumentName = currentNode.getNodeName();
                switch (argumentName) {
                    case "ID":
                    case "Literal": {
                        String temp = this.newTemp();
                        Cuadruplos.GEN(":=", currentNode.getAttribute("Value"), temp);
                        parameters.add(temp);
                        break;
                    }
                    case "ARRAY": {
                        this.cuadruploArray(currentNode);
                        String temp = this.getTemp();
                        parameters.add(temp);
                        break;
                    }
                    case "AND":
                    case "OR":
                    case "NOT":
                    case "GreaterThan":
                    case "LessThan":
                    case "Equals":
                    case "LessOrEqual":
                    case "GreaterOrEqual":
                    case "Different": {
                        cuadruploRelacional(currentNode);
                        String newTemp = this.newTemp();
                        int M1 = Cuadruplos.getSize();
                        Cuadruplos.GEN(":=", "1", "", newTemp);
                        Cuadruplos.GEN_GOTO(Cuadruplos.getSize() + 2 + "");
                        int M2 = Cuadruplos.getSize();
                        Cuadruplos.GEN(":=", "0", "", newTemp);
                        this.completa(M1, currentNode.getAttribute("listaV"));
                        this.completa(M2, currentNode.getAttribute("listaF"));
                        parameters.add(newTemp);
                        break;
                    }

                    case "Div":
                    case "Minus":
                    case "Times":
                    case "Plus": {
                        cuadruploAritmetico(currentNode);
                        String temp = this.getTemp();
                        parameters.add(temp);
                        break;
                    }
                }
            }

            for (String Param : parameters) {
                Cuadruplos.GEN_PARAM(Param);
            }

            String functionName = funcId.getAttribute("Value");
            Cuadruplos.GEN_CALL(functionName);
        }

    }

    public String crearLista(int quadIndex) {
        return "" + quadIndex;
    }

    public String fusiona(String list1, String list2) {
        return list1 + "," + list2;
    }

    public void completa(int quadIndex, String lista) {
        String[] splitList = lista.split(",");
        for (String indexS : splitList) {
            int index = Integer.valueOf(indexS);
            Cuadruplo quad = Cuadruplos.cuadruplos.get(index);
            if (quad.operacion.equals("GOTO")) {
                quad.arg1 = quadIndex + "";
            } else {
                quad.resultado = quadIndex + "";
            }
        }
    }

    public void print() {
        Cuadruplos.print();
    }

    private String getTypeSize(String tipo) {
        if (tipo.equals("char") || tipo.equals("boolean")) {
            return "1";
        } else {
            return "4";
        }
    }

    private String getTemp() {
        return "$t" + this.tempCounter;
    }

    private String newTemp() {
        return "$t" + ++this.tempCounter;
    }
}
