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
import java.io.*;
import java.io.FileReader;
import java_cup.runtime.Symbol;
import java_cup.runtime.XMLElement;
import java.io.File;
import java.io.BufferedReader;
import org.w3c.dom.Element;
public class PascalCompiler {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
      try {
          String path = "C:\\Users\\soporte\\Desktop\\pascalCompilador\\test2.pp";
          
      parser p = new parser(new PascalFlexer(new FileReader(path)));
      Element element = (Element) p.parse().value;
      System.out.println(element);
      SemanticParser.llenarTablaSimbolos(element);
    } catch (Exception e) {
      e.printStackTrace();
    }
    }
    
}
