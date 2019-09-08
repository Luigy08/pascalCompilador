
import java.io.*;
import java.io.FileReader;
import java_cup.runtime.Symbol;
import java_cup.runtime.XMLElement;
import java.io.File;
import java.io.BufferedReader;

 public class Main {
  static public void main(String argv[]) {    
    /* Start the parser */
    try {
      parser p = new parser(new PascalFlexer(new FileReader(argv[0])));
      p.parse(); 
    } catch (Exception e) {
      e.printStackTrace();
    }
   } 
}