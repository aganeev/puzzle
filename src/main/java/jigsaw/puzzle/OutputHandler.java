package jigsaw.puzzle;

import jigsaw.puzzle.entities.Report;

import java.io.*;

class OutputHandler {
    private OutputHandler(){}

    static void reportToFile(Report report, String outputPath) throws IOException {

        /***************************************************************************************************
         *  the exception is in the signature and not in try/catch, because the app should crash if there's
         *  a problem writing the output file
         ***************************************************************************************************/


        OutputStream outputFile = (new FileOutputStream(outputPath));
        OutputStreamWriter out = new OutputStreamWriter(outputFile);
        out.write(report.isEmpty()? "Error::Report is empty" : report.toString());
        out.close();
    }
}
