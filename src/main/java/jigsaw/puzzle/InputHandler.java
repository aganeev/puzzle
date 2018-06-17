package jigsaw.puzzle;

import com.google.gson.*;
import jigsaw.puzzle.entities.Piece;
import jigsaw.puzzle.entities.Report;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class InputHandler {
    private Report report;

    private static final Logger logger = LogManager.getLogger(InputHandler.class.getName());

    InputHandler(Report report) {
        this.report = report;
    }

    Set<Piece> readFromJson(String jsonString) {
        logger.debug("Parsing JSON - start");
        JsonElement json = null;
        Set<Piece> pieces = new HashSet<>();
        try {
            json = new JsonParser().parse(jsonString);
        } catch (JsonParseException jEx) {
            String error = "Received request is not a valid JSON";
            logger.error(error);
            report.addErrorLine(error);
        }
        if (json != null) {
            JsonElement puzzle = json.getAsJsonObject().get("puzzle");
            if (puzzle == null) {
                String error = "json haven't puzzle object";
                logger.error(error);
                report.addErrorLine(error);
            } else {
                JsonElement piecesJson = puzzle.getAsJsonObject().get("pieces");
                if (piecesJson == null) {
                    String error = "json haven't pieces object";
                    logger.error(error);
                    report.addErrorLine(error);
                } else {
                    JsonArray piecesArray;
                    try {
                        piecesArray = piecesJson.getAsJsonArray();
                        Gson gson = new Gson();
                        piecesArray.forEach(jsonPiece-> pieces.add(gson.fromJson(jsonPiece,Piece.class)));
                    } catch (IllegalStateException ex) {
                        String error = "Pieces object is not valid array";
                        logger.error(error);
                        report.addErrorLine(error);
                    }

                }
            }
        }
        List piecesString = pieces.stream().map(Piece::fullPieceToString).collect(Collectors.toList());
        logger.debug("Parsed pieces: {}", piecesString);
        logger.debug("Parsing JSON - stop");
        return pieces;
    }

    Set<Piece> readFromFile(String file) {

        String wrongElementsMsg = "";
        ArrayList<String> wrongElementsFormat = new ArrayList<>();
        int puzzleSize = 0;
        int actualLineCounter = 0;
        Piece newPiece;
        Set<Piece> retValue = new HashSet<>();

        try(BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"))) {
            String line;

            while ((line = in.readLine()) != null) {
                char firstLetter = ' ';
                boolean validPiece = true;
                line = line.trim();
                if (line.length() != 0) {
                    firstLetter = line.charAt(0);
                }

                char comment = '#';
                if ( (line.length() == 0) || (firstLetter == comment)) {
                    continue;
                }
                if (actualLineCounter == 0) {
                    String[] pieceValues = line.split("=");
                    puzzleSize = Integer.parseInt(pieceValues[1]);
                } else {
                    String[] pieceValues = line.split(" ");

                    int id = Integer.parseInt(pieceValues[0]);

                    // if there are less or more than 4 edges
                    if (pieceValues.length !=5) {
                        wrongElementsFormat.add(line);
                        continue;
                    }
                    int left = Integer.parseInt(pieceValues[1]);
                    int up = Integer.parseInt(pieceValues[2]);
                    int right = Integer.parseInt(pieceValues[3]);
                    int bottom = Integer.parseInt(pieceValues[4]);
                    newPiece = new Piece(id, new int[]{left, up, right, bottom});

                    // checking if the edges are 0/1/-1
                    if (!pieceEdgesAreValid(newPiece)) {
                        wrongElementsFormat.add(line);
                        validPiece = false;
                    }

                    // checking if the id of the piece isn't bigger than numElements
                    if (!pieceIdIsValid(newPiece, puzzleSize)) {
                        wrongElementsMsg += newPiece.getId() + ",";
                        validPiece = false;
                    }

                    if (validPiece) {
                        retValue.add(newPiece);
                    }

                }

                ++actualLineCounter;
            }
        } catch (UnsupportedEncodingException e) {
            report.addErrorLine("Error::Unsupported Encoding Exception");
        } catch (FileNotFoundException e) {
            report.addErrorLine("Error::File Not Found");
            System.out.println("Error::File Not Found");
        } catch (IOException e) {
            report.addErrorLine("Error:IOException");
        }

        // creating the report for the elements that aren't on the list
        checkMissingElements(retValue, puzzleSize);
        checkWrongElementsMsg(wrongElementsMsg, puzzleSize);
        checkWrongElementsFormat(wrongElementsFormat);

        return retValue;
    }

    private void checkMissingElements(Set<Piece> set, int puzzleSize) {
        String[] array = new String[puzzleSize];
        for (int i = 0; i < puzzleSize; i++) {
            array[i] = (i + 1) + "";
        }


        for (Piece p : set) {
            array[p.getId() - 1] = "";
        }

        String missingElementsMsg = "";
        for (String str : array) {
            if (!str.isEmpty()) {
                missingElementsMsg += str + ",";
            }

        }

        // removing the last comma
        int msgSize = missingElementsMsg.length() - 1;
        if (missingElementsMsg.length() > 0) {
            missingElementsMsg = missingElementsMsg.substring(0, msgSize);
            report.addErrorLine("Missing puzzle element(s) with the following IDs: " + missingElementsMsg);
            System.out.println("Missing puzzle element(s) with the following IDs: " + missingElementsMsg);
        }
    }

    private boolean pieceEdgesAreValid(Piece p) {
        if (p.getTop() > 1 ||  p.getTop() < -1) {
            return false;
        }
        if (p.getRight() > 1 || p.getRight() < -1) {
            return false;
        }
        if (p.getBottom() > 1 || p.getBottom() < -1) {
            return false;
        }
        return p.getLeft() <= 1 && p.getLeft() >= -1;
    }

    private boolean pieceIdIsValid(Piece p, int puzzleSize) {
        return p.getId() <= puzzleSize;
    }

    private void checkWrongElementsMsg(String wrongElementsMsg, int puzzleSize) {
        // creating the report for the elements are have ID bigger than numElements
        if (!wrongElementsMsg.isEmpty()) {
            int msgSize = wrongElementsMsg.length() - 1;
            wrongElementsMsg = wrongElementsMsg.substring(0, msgSize);
            report.addErrorLine("Puzzle of size " +  puzzleSize + " cannot have the following ID(s): " + wrongElementsMsg);
            System.out.println("Puzzle of size " +  puzzleSize + " cannot have the following ID(s): " + wrongElementsMsg);
        }
    }

    private void checkWrongElementsFormat(ArrayList<String> wrongElementsFormat) {
        // creating arraylist of wrongElementsFormat error messages
        if (!wrongElementsFormat.isEmpty()) {
            for (String msg: wrongElementsFormat) {
                report.addErrorLine("Puzzle ID " + msg.charAt(0) + " has wrong data: " + msg);
                System.out.println(("Puzzle ID " + msg.charAt(0) + " has wrong data: " + msg));
            }
        }
    }
}
