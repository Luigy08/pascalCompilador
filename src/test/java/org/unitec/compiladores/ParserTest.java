package org.unitec.compiladores;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java_cup.runtime.Symbol;
import org.junit.Assert;
import org.junit.Test;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Cesar Bonilla
 */

public class ParserTest {
    
    Logger logger = Logger.getLogger(this.getClass().getName());
    
    @Test
    public void testParser(){
        PascalFlexer scanner;
        Parser parser = null;
        Symbol parseSymbol = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader("src/main/resources/good1.pas"));
            scanner = new PascalFlexer(br);
            parser = new Parser(scanner);
            
            //parseSymbol = parser.debug_parse();
            parseSymbol = parser.parse();
            
            
        } catch (FileNotFoundException  ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ParserTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(parseSymbol == null)
            Assert.assertFalse(true);
        else 
            Assert.assertEquals(0, parseSymbol.sym);
        
    }    
}
