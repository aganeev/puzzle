package jigsaw.puzzle;

import jigsaw.puzzle.entities.Report;

import java.io.*;

class OutputHandler {
    private Report report;

    OutputHandler(Report report) {
        this.report = report;
    }

    void reportToFile(String outputPath) {

       // In case of file writing exceptions: print usage to the user with relevant info, but not the full stack trace.
        try (OutputStream outputFile = new FileOutputStream(outputPath);
             OutputStreamWriter out = new OutputStreamWriter(outputFile);
             BufferedWriter br = new BufferedWriter(out)) {
            if (report.hasErrors()) {
                for (String error : report.getErrors()) {
                    br.write(error);
                    br.newLine();
                }
            } else if (report.hasSolution()) {
                for (String line : convertToLines(report.getSolution())) {
                    br.write(line);
                    br.newLine();
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: Output file not found");
        } catch (UnsupportedEncodingException e) {
            System.err.println("Error: Output file with unsupported encoding");
        } catch (IOException e) {
            System.err.println("Error: IO exception during output file writing");
        }
    }

    private String[] convertToLines(int[] solution) {
        String[] returnValue = new String[(solution.length - 1) / solution[0]];
        int k = 1;
        for (int i = 0; i < returnValue.length; i++) {
            StringBuilder line = new StringBuilder();
            for (int j = 0; j < solution[0]; j++) {
                line.append(solution[k++]).append(" ");
            }
            returnValue[i] = line.toString();
        }
        return returnValue;
    }
}
