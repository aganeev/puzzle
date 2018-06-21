package jigsaw.puzzle;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jigsaw.puzzle.entities.Piece;
import jigsaw.puzzle.entities.Report;
import jigsaw.puzzle.entities.PieceSet;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class PuzzleMainClient {
    private static final Logger logger = LogManager.getLogger(PuzzleMainClient.class.getName());

    private static final String PORT_PARAM_NAME = "port";
    private static final String PORT_DEFAULT_VALUE = "7095";
    private static final String HOST_PARAM_NAME = "host";
    private static final String HOST_DEFAULT_VALUE = "127.0.0.1";

    public static void main(String[] args) {

        Map<String,String> params = parseArgs(args);
        int port = validatePort(params.getOrDefault(PORT_PARAM_NAME,PORT_DEFAULT_VALUE));
        String host = validateHost(params.getOrDefault(HOST_PARAM_NAME,HOST_DEFAULT_VALUE));

        String inputPath = params.get("input");

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
                  Socket socket = new Socket(host, port);
                  BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
                  PrintStream socketOutput = new PrintStream(socket.getOutputStream(), /* autoflush */ true, "UTF8")
            ) {

                String response;
                logger.info("Sending JSON to server...");
                socketOutput.println(puzzle.toString());
                while ((response = socketInput.readLine()) != null) {
                    logger.info(response);
                    if (response.contains("puzzleSolution")) {
                        report.addLine(response);
                    }


                }


            } catch (UnsupportedEncodingException e) {
                logger.error("Wrong encoding problem...");
            } catch (UnknownHostException e) {
                logger.error("Unknown problem occured...");
            } catch (IOException e) {
                logger.error("IOException error...");
            }
        }

        String outputPath = params.get("input") != null? params.get("input") : "/src/test/resources/output.txt";
        OutputHandler outputHandler = new OutputHandler(report, outputPath);
    }

    private static int validatePort(String value) {
        String error = String.format("The port is wrong (%s), only ports in range 1024–49151 are available",value);
        if (!value.matches("\\d+")) {
            exitWithError(error);
        }
        int intValue = Integer.parseInt(value);
        if  (intValue < 1024 || intValue > 49151) {
            exitWithError(error);
        }
        return intValue;
    }

    private static String validateHost(String value) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        String error = String.format("The host is invalid: (%s), The host ip address format should be: xxx.xxx.xxx.xxx",value);
        if (!value.matches(PATTERN)) {
            exitWithError(error);
        }
        return value;
    }

    private static Map<String,String> parseArgs(String[] args) {
        Map<String, String> parsedArgs = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            String param = args[i];
            if (param.matches("-[a-zA-Z]+")) {
                parsedArgs.put(param.substring(1), args[++i]);
            }
        }
        return parsedArgs;
    }

    private static void exitWithError(String error) {
        logger.fatal(error);
        System.exit(1);
    }
}
