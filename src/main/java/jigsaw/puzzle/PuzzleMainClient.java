package jigsaw.puzzle;

import jigsaw.puzzle.entities.Piece;
import jigsaw.puzzle.entities.Report;
import jigsaw.puzzle.entities.PieceSet;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.google.gson.Gson;

import static jigsaw.puzzle.PuzzleMainServer.logger;

public class PuzzleMainClient {

    public static void main(String[] args) {

        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        logger.setLevel(Level.INFO);

        String inputPath = "C:\\Users\\od104b\\IdeaProjects\\puzzle\\src\\test\\resources\\solvable.txt";

        Report report = new Report();
        InputHandler inputHandler = new InputHandler(report);
        Set<Piece> pieces = inputHandler.readFromFile(inputPath);
        PieceSet pieceSet = new PieceSet(pieces);
        Gson gson = new Gson();
        String jsonContent = gson.toJson(pieceSet);

        System.out.println(jsonContent);

        try ( // try with resource for all the below
              Socket socket = new Socket("localhost", 7095);
              BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
              PrintStream socketOutput = new PrintStream(socket.getOutputStream(), /* autoflush */ true, "UTF8");
        ) {

            String response = "";
            logger.info("Sending JSON to server...");
            socketOutput.println(jsonContent);
            while (!response.equals("exit")) {
                response = socketInput.readLine();
                System.out.println(response.toString());
                logger.info(response);
            }


        } catch (UnsupportedEncodingException e) {
            logger.log(Level.SEVERE, "Wrong encoding problem...");
        } catch (UnknownHostException e) {
            logger.log(Level.SEVERE, "Unknown problem occured...");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IOException error...");
        }

    }
}
