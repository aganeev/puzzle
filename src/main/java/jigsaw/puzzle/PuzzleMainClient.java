package jigsaw.puzzle;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jigsaw.puzzle.entities.Piece;
import jigsaw.puzzle.entities.Report;
import jigsaw.puzzle.entities.PieceSet;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Set;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class PuzzleMainClient {
    private static final Logger logger = LogManager.getLogger(PuzzleMainClient.class.getName());

    public static void main(String[] args) {


        String inputPath = "src/test/resources/notSolvable4x4";

        Report report = new Report();
        InputHandler inputHandler = new InputHandler(report);
        Set<Piece> pieces = inputHandler.readFromFile(inputPath);
        if (!report.hasErrors() && !pieces.isEmpty()) {
            PieceSet pieceSet = new PieceSet(pieces);
            Gson gson = new Gson();
            JsonElement piecesJson = gson.toJsonTree(pieceSet);
            JsonObject puzzle = new JsonObject();
            puzzle.add("puzzle", piecesJson);
            logger.debug(puzzle.toString());

            try ( // try with resource for all the below
                  Socket socket = new Socket("localhost", 7095);
                  BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
                  PrintStream socketOutput = new PrintStream(socket.getOutputStream(), /* autoflush */ true, "UTF8")
            ) {

                String response;
                logger.info("Sending JSON to server...");
                socketOutput.println(puzzle.toString());
                while ((response = socketInput.readLine()) != null) {
                    logger.info(response);
                }


            } catch (UnsupportedEncodingException e) {
                logger.error("Wrong encoding problem...");
            } catch (UnknownHostException e) {
                logger.error("Unknown problem occured...");
            } catch (IOException e) {
                logger.error("IOException error...");
            }
        }
    }
}
