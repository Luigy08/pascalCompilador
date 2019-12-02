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
import java.util.ArrayList;


public class SymbolTable {

    public ArrayList<Symbol> SymbolArray = new ArrayList();

    public int Add(Symbol symbol) throws Exception {
        SymbolArray.add(symbol);
        return SymbolArray.size() -1;
    }
    
    public Symbol getSimbolo(int index) throws Exception {
        if(index >= 0 && index < SymbolArray.size()){
            return SymbolArray.get(index);
        } else {
            throw new Exception("Symbol not found");
        }
    }
    
     public Symbol getVariable(String Id) throws Exception {
        for(Symbol S : SymbolArray){
            if(S.getId().equals(Id) && S.isVariable()){
                return S;
            }
        }  
        return null;
    }
     
     public Symbol getFunction(String Id) throws Exception {
        for(Symbol S : SymbolArray){
            if(S.getId().equals(Id) && S.isFunction()){
                return S;
            }
        }  
        return null;
    }

    public void replaceNode(Symbol S, int index) {
        SymbolArray.set(index, S);
    }
    
    public void clear(){
        SymbolArray.clear();
    }
    
    @Override
    public String toString() {
        System.out.format("%-16s%-24s%-10s%-10s%-10s", "ID", "Tipo", "Variable", "Función", "Parametro de Función");
        System.out.println();
        
        for (Symbol S: SymbolArray) {
            System.out.format("%-16s%-24s%-10s%-10s%-10s",S.getId(),S.getType(),String.valueOf(S.isVariable()),String.valueOf(S.isFunction()), String.valueOf(S.isParameter()));
            System.out.println();
        }
        
        return ""; 
    }
}