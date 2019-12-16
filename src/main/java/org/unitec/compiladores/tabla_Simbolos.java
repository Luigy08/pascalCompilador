/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unitec.compiladores;

import java.util.ArrayList;
import javax.swing.JTextArea;

/**
 *
 * @author Cesar Bonilla
 */
public class tabla_Simbolos {

    public ArrayList<Simbolo> Simbolos = new ArrayList();
    
    public int Add(Simbolo S) throws Exception {
        int itemIndex = this.getSymbolIndex(S);
        if(itemIndex > 0) {
            throw new Exception("Ya existe un elemento " + S.getId() + " en el ambito " + S.getAmbito());
        } else {
            Simbolos.add(S);
        }
        return Simbolos.size() -1;
    }
    
    public Simbolo getSimbolo(int index) throws Exception {
        if(index >= 0 && index < Simbolos.size()){
            return Simbolos.get(index);
        } else {
            throw new Exception("Symbol not found");
        }
    }
    
     public Simbolo getVariable(String Id) throws Exception {
        for(Simbolo S : Simbolos){
            if(S.getId().equals(Id) && S.isVariable()){
                return S;
            }
        }  
        return null;
    }
     
     public Simbolo getFunction(String Id) throws Exception {
        for(Simbolo S : Simbolos){
            if(S.getId().equals(Id) && S.isFuncion()){
                return S;
            }
        }  
        return null;
    }
     
    public Simbolo getVariable(String Id, String ambito) throws Exception {
        for(Simbolo S : Simbolos){
            if(S.getId().equals(Id) && (S.isVariable() || S.isParametro()) && S.getAmbito().equals(ambito)){
                return S;
            }
        }  
        return null;
    }
    
    public int getSymbolIndex(Simbolo S){
        for (int i = 0; i < Simbolos.size(); i++) {
            Simbolo St = Simbolos.get(i);
            boolean hasSameName = S.getId().equals(St.getId());
            boolean hasSameScope = S.getAmbito().equals(St.getAmbito());
            if( hasSameName && hasSameScope){
                return i;
            }
        }
        return -1;
    } 
    
    public void replaceNode(Simbolo S, int index) {
        Simbolos.set(index, S);
    }
    
    private boolean hasSameParameters(Simbolo S1, Simbolo S2) {
        return false;
    }
    
    public void clear(){
        Simbolos.clear();
    }
    public JTextArea printSimbolTable () {
        JTextArea txt = new JTextArea();
        String formatHeader = "%-20s %-70s %-15s %-15s %-15s %-15s %-15s %-18s";
        String formatBody = "%-20s %-70s %-25s %-25s %-25s %-25s %-25s %-28s";
        String headers = String.format(
            formatHeader,
            "ID",
            "TIPO",
            "AMBITO",
            "VARIABLE",
            "FUNCION",
            "PARAMETRO",
            "REF",
            "POSICION MEMORIA"
        );
        txt.append(headers + "\n");
        for (Simbolo S: Simbolos) {
            String output = String.format(
                    formatBody,
                    S.getId(),
                    S.getTipo(),
                    S.getAmbito(),
                    String.valueOf(S.isVariable()),
                    String.valueOf(S.isFuncion()),
                    String.valueOf(S.isParametro()),
                    String.valueOf(S.isByRef()),
                    String.valueOf(S.getPosicionMemoria())
            );
            txt.append(output + "\n");
        }
        return txt;
    }
    @Override
    public String toString() {
        return "";
    }
}
