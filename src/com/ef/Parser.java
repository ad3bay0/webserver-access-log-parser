/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef;

/**
 *
 * @author Adebeslick
 */
public class Parser {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        if (args.length == 4) {
            
           try{
               
                ParserUtil.isVaildArguments(args[0], args[1], args[2], args[3]);
               
           }catch(Exception ex){
               
               ex.printStackTrace();
           }
           

        } else {
            
            System.out.println("Please provide command line arguments");
        }
    }

}
