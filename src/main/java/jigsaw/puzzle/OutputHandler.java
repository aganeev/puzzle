package jigsaw.puzzle;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jigsaw.puzzle.entities.Report;

import java.io.*;
import java.util.List;

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
            } else {
                br.write("Cannot solve puzzle: it seems that there is no proper solution");
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
        int numberOfRows = solution[0];
        int numbersInRow = (solution.length - 1) / numberOfRows;
        String[] returnValue = new String[numberOfRows];

        int k = 1;
        for (int i = 0; i < numberOfRows; i++) {
            StringBuilder line = new StringBuilder();
            for (int j = 0; j < numbersInRow; j++) {
                line.append(solution[k++]).append(" ");
            }
            returnValue[i] = line.toString();
        }
        return returnValue;
    }

    void reportJsonToSocket(PrintStream socketOutput) {
        String response;
            if (report.hasErrors()) {
                response = createErrorJson();
            } else if (report.hasSolution()) {
                response = createSolutionJson();
            } else {
                response = "{\"PuzzleSolution\": {\"SolutionExists\": false}}";
            }
        socketOutput.print(response);
        socketOutput.flush();
        socketOutput.close();
    }

    private String createSolutionJson() {
        JsonArray solutionPiecesJson = new JsonArray();
        int[] solution = report.getSolution();
        for (int i = 1; i < solution.length; i++) {
            solutionPiecesJson.add(solution[i]);
        }
        JsonObject solutionJson = new JsonObject();
        solutionJson.add("solutionPieces", solutionPiecesJson);
        solutionJson.addProperty("rows", solution[0]);
        JsonObject response = new JsonObject();
        response.add("puzzleSolution", solutionJson);
        return response.toString();
    }

    private String createErrorJson() {
        JsonArray errorsJson = new JsonArray();
        report.getErrors().forEach(errorsJson::add);
        JsonObject errorsElement = new JsonObject();
        errorsElement.add("errors", errorsJson);
        JsonObject response = new JsonObject();
        response.add("puzzleSolution", errorsElement);
        return response.getAsString();
    }


}
