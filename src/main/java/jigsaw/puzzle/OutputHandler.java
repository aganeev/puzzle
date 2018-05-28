package jigsaw.puzzle;

import jigsaw.puzzle.entities.Report;

import java.io.*;

class OutputHandler {
    private OutputHandler(){}

    static void reportToFile(Report report, String outputPath) {

       // In case of file writing exceptions: print usage to the user with relevant info, but not the full stack trace.
        try (OutputStream outputFile = new FileOutputStream(outputPath);
             OutputStreamWriter out = new OutputStreamWriter(outputFile)) {
            out.write(report.isEmpty() ? "Error::Report is empty" : report.toString());
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found");
        } catch (UnsupportedEncodingException e) {
            System.out.println("Error: Using unsupported encoding");
        } catch (IOException e) {
            System.out.println("Error: IO exception");
        }
    }
}
