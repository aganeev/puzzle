package jigsaw.puzzle;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jigsaw.puzzle.entities.Report;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

class OutputHandler {
    private Report report;

    private static final Logger logger = LogManager.getLogger(OutputHandler.class.getName());

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
            logger.error("Output file not found");
        } catch (UnsupportedEncodingException e) {
            logger.error("Output file with unsupported encoding");
        } catch (IOException e) {
            logger.error("IO exception during output file writing");
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
        JsonObject puzzleSolution = new JsonObject();
        boolean isSolutionExist = false;
        if (report.hasErrors()) {
            puzzleSolution.add("errors", createErrorJson());
        } else if (report.hasSolution()) {
            isSolutionExist = true;
            puzzleSolution.add("solution", createSolutionJson());
        }
        puzzleSolution.addProperty("solutionExists",isSolutionExist);
        JsonObject response = new JsonObject();
        response.add("puzzleSolution", puzzleSolution);
        logger.debug("Sending final response: {}", response);
        socketOutput.print(response.toString());
        socketOutput.flush();
        socketOutput.close();
    }

    private JsonElement createSolutionJson() {
        JsonArray solutionPiecesJson = new JsonArray();
        int[] solution = report.getSolution();
        for (int i = 1; i < solution.length; i++) {
            solutionPiecesJson.add(solution[i]);
        }
        JsonObject solutionJson = new JsonObject();
        solutionJson.add("solutionPieces", solutionPiecesJson);
        solutionJson.addProperty("rows", solution[0]);
        return solutionJson;
    }

    private JsonElement createErrorJson() {
        JsonArray errorsJson = new JsonArray();
        report.getErrors().forEach(error->{
            logger.error(error);
            errorsJson.add(error);
        });
        return errorsJson;
    }


    void validateOutputFilePath(String outputPath) {
        File file = new File(outputPath);
        file = file.getAbsoluteFile();
        if (file.isDirectory()) {
            logger.error("Value should be a file and not a directory.");
            System.exit(1);
        }
        file = file.getParentFile();
        if (!file.exists()){
            logger.error("Given directory doesn't exist");
            System.exit(1);
        }
    }
}
