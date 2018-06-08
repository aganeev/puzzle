package jigsaw.puzzle;

import jigsaw.puzzle.entities.Piece;
import jigsaw.puzzle.entities.Report;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class InputHandler {
    private Report report;
    private int pieceSetSize = 0;
    private Map<String,List<String>> errorMap;
    private Set<Piece> pieceSet;
    private boolean isPuzzleSizeParamErrorAlreadyAdded;

    private static final String EQUALITY_SIGN = "=";
    private static final String COMMENT_SIGN = "#";
    private static final String PUZZLE_SIZE_PARAM_NAME = "NumElements";

    InputHandler(Report report) {
        this.report = report;
        errorMap = new LinkedHashMap<>();
        pieceSet = new HashSet<>();
    }

    Set<Piece> readFromFile(String path) {
        Path filePath = Paths.get(path);
        if (!filePath.toFile().exists()) {
            System.err.println("Error: Input file not found");
        } else {
            try (Stream<String> linesStream = Files.lines(filePath)) {
                linesStream.map(String::trim)
                        .filter(line -> !line.startsWith(COMMENT_SIGN))
                        .filter(line -> !line.isEmpty())
                        .forEach(line -> {
                            if (line.contains(PUZZLE_SIZE_PARAM_NAME)) {
                                updateSizeValue(line);
                            } else if (pieceSetSize != 0) {
                                handlePiece(line);
                            } else {
                                addWrongPuzzleSizeParamError();
                            }
                        });
            } catch (IOException e) {
                System.err.println("Error: IO exception during input file reading");
            } finally {
                collectErrors();
            }
        }
        return pieceSet;
    }

    private void addWrongPuzzleSizeParamError() {
        if (!isPuzzleSizeParamErrorAlreadyAdded) {
            isPuzzleSizeParamErrorAlreadyAdded = true;
            errorMap.put(String.format("Error: Parameter '%s' is not found",PUZZLE_SIZE_PARAM_NAME), null);
        }

    }

    private void collectErrors() {
        if (pieceSetSize != 0) {
            Set<Integer> idSet = pieceSet.stream().map(Piece::getId).collect(Collectors.toSet());
            for (int i = 1; i <= pieceSetSize; i++) {
                if (!idSet.contains(i)) {
                    String error = "Missing puzzle element(s) with the following ID(s):";
                    List<String> idList = errorMap.getOrDefault(error, new ArrayList<>());
                    idList.add(String.valueOf(i));
                    errorMap.put(error, idList);
                }
            }
        }
        if (!errorMap.isEmpty()) {
            pieceSet.clear();
            errorMap.forEach((error,ids)->{
                String finalError = error;
                if (ids != null) {
                    finalError += ids.stream().collect(Collectors.joining(", ", " ",""));
                }
                report.addErrorLine(finalError);
            });
        }
    }

    private void handlePiece(String pieceLine) {
        String[] splittedString = pieceLine.split("\\s+");
        int id;
        int[] edges = new int[4];
        String idString = splittedString[0];
        if (!idString.matches("\\d+")) {
            errorMap.put(String.format("Wrong Puzzle ID value (%s). Expected: integer",idString), null);
            return;
        }
        id = Integer.valueOf(idString);
        if (id == 0) {
            errorMap.put("Wrong Puzzle ID value. Cannot be 0", null);
            return;
        }
        if (id > pieceSetSize) {
            String error = String.format("Puzzle of size %s cannot have the following ID(s):",pieceSetSize);
            List<String> idList = errorMap.getOrDefault(error, new ArrayList<>());
            idList.add(idString);
            errorMap.put(error, idList);
            return;
        }
        if (splittedString.length != 5) {
            errorMap.put(String.format("Puzzle ID %s has wrong data: %s",id, pieceLine), null);
            return;
        }
        for (int i = 1; i < splittedString.length; i++) {
            String edge = splittedString[i];
            if (!edge.matches("(-?1)|(0)")) {
                errorMap.put(String.format("Puzzle ID %s has wrong data: %s",idString, pieceLine), null);
                return;
            } else {
                edges[i-1] = Integer.valueOf(edge);
            }
        }
        if (!pieceSet.add(new Piece(id, edges))) {
            errorMap.put(String.format("Puzzle ID %s has specified more than one time",idString), null);
        }
    }

    private void updateSizeValue(String line) {
        int indexOfEqualitySign = line.indexOf(EQUALITY_SIGN);
        if (indexOfEqualitySign >= 0) {
            String[] splittedString = line.split(EQUALITY_SIGN);
            if (splittedString.length != 2 || !PUZZLE_SIZE_PARAM_NAME.equals(splittedString[0].trim())) {
                errorMap.put(String.format("Wrong %s parameter format. Expected: %<s=<number>",PUZZLE_SIZE_PARAM_NAME), null);
            } else {
                String sizeValue = splittedString[1].trim();
                if (!sizeValue.matches("\\d+")) {
                    errorMap.put(String.format("Wrong %s parameter value (%s). Expected integer",PUZZLE_SIZE_PARAM_NAME, sizeValue), null);
                } else {
                    pieceSetSize = Integer.valueOf(sizeValue);
                }
            }
        } else {
            errorMap.put(String.format("Wrong %s parameter format. Expected: %<s=<number>",PUZZLE_SIZE_PARAM_NAME), null);
        }
        isPuzzleSizeParamErrorAlreadyAdded = true;
    }

}
