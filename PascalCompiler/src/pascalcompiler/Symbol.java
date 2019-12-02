/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pascalcompiler;

/**
 *
 * @author soporte
 */
public class Symbol {
    private String id;
    private String value;
    private String type;
    private int refIndex = -1;
    private boolean variable = false;
    private boolean function = false;
    private boolean parameter = false;
    private boolean byRef = false;

    public int getRefIndex() {
        return refIndex;
    }

    public void setRefIndex(int refIndex) {
        this.refIndex = refIndex;
    }
    
    public boolean isByRef() {
        return byRef;
    }

    public void setByRef(boolean byRef) {
        this.byRef = byRef;
    }

    
    public Symbol() {
        this.id = "";
        this.value = "";
        this.type = "";
        this.variable = false;
        this.function = false;
        this.parameter = false;
        
    }
    
     public Symbol(String id, String valor, String tipo) {
        this.id = id;
        this.type = tipo;
        this.value = valor;
    }
     
    public Symbol(String id, String value, String type, boolean variable, boolean function, boolean parameter) {
        this.id = id;
        this.value = value;
        this.type = type;
        this.variable = variable;
        this.function = function;
        this.parameter = parameter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValor(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isVariable() {
        return variable;
    }

    public void setVariable(boolean variable) {
        this.variable = variable;
    }

    public boolean isFunction() {
        return function;
    }

    public void setFunction(boolean function) {
        this.function = function;
    }

    public boolean isParameter() {
        return parameter;
    }

    public void setParameter(boolean parameter) {
        this.parameter = parameter;
    }

}