package com.ef;

/**
 * Main class
 *
 * @author Adebayo Adeniyan
 */
public class Parser {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        ParserUtil.cleanAllDatabase();

//confirm arguments contain filepath, start date, duration, threshold arguments
        if (args.length == 4) {

            try {

                String filePath = args[0];
                String startDateString = args[1];
                String duration = args[2];
                String threshold = args[3];

                ParserUtil.processArgumentQuery(filePath, startDateString, duration, threshold);

            } catch (Exception ex) {

                ex.printStackTrace();
            }

        } else {

            System.out.println("Please provide command line arguments");
        }
    }

}
