package jigsaw.puzzle;

import jigsaw.puzzle.entities.Piece;
import jigsaw.puzzle.entities.Report;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

public class PuzzleMainServer {

    private static final String THREADS_PARAM_NAME = "threads";
    private static final String THREADS_DEFAULT_VALUE = "4";
    private static final String PORT_PARAM_NAME = "port";
    private static final String PORT_DEFAULT_VALUE = "7095";

    private static final Logger logger = LogManager.getLogger(PuzzleMainServer.class.getName());

    public static void main(String[] args) {
        Map<String,String> params = parseArgs(args);
        int numOfThreads = validateNumOfThreads(params.getOrDefault(THREADS_PARAM_NAME,THREADS_DEFAULT_VALUE));
        int port = validatePort(params.getOrDefault(PORT_PARAM_NAME,PORT_DEFAULT_VALUE));
        PuzzleMainServer puzzleMainServer = new PuzzleMainServer();
        puzzleMainServer.start(numOfThreads, port);


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

    private static int validateNumOfThreads(String value) {
        String error = String.format("The number of threads is wrong (%s), can be only positive integer less than 10",value);
        if (!value.matches("\\d+")) {
            exitWithError(error);
        }
        int intValue = Integer.parseInt(value);
        if  (intValue > 10) {
            exitWithError(error);
        }
        return intValue;
    }

    private static Map<String,String> parseArgs(String[] args) {
        Map<String,String> parsedArgs = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            String param = args[i];
            if (param.matches("-[a-zA-Z]+")) {
                parsedArgs.put(param.substring(1),args[++i]);
            }
        }
        return parsedArgs;
    }

    private void start(int numOfThreads, int port) {
        try (ServerSocket listener = new ServerSocket(port)) {
            logger.info("Server is up on port={} and number of threads={}.",port,numOfThreads);
            ForkJoinPool myPool = new ForkJoinPool(numOfThreads);
            while (!listener.isClosed()) {
                Socket socket = listener.accept();
                String sessionId = UUID.randomUUID().toString();
                ThreadContext.push(sessionId);
                logger.debug("Request received. Generated session id {}",sessionId);
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
                PrintStream socketOutput = new PrintStream(socket.getOutputStream(), true, "UTF8");
                String request = socketInput.readLine();
                logger.debug("Received request: {}", request);
                Report report = new Report();
                InputHandler inputHandler = new InputHandler(report);
                Set<Piece> pieces = inputHandler.readPiecesFromJson(request);
                String immediateResponse = String.format("{\"puzzleReceived\":{\"sessionId\":\"%s\",\"numPieces\":%s}}",sessionId,pieces.size());
                socketOutput.println(immediateResponse);
                logger.debug("Sending immediate response: {}", immediateResponse);
                socketOutput.flush();
                myPool.execute(() -> {
                    try {
                        ThreadContext.push(sessionId);
                        handleRequest(socketOutput, pieces, report);
                        socketInput.close();
                        socketOutput.close();
                        socket.close();
                    } catch (IOException e) {
                        logger.error("Got an IOException during request handling: {}", e);
                    } finally {
                        ThreadContext.pop();
                    }
                });
                ThreadContext.pop();
            }

        } catch (BindException bindException) {
            logger.error("Port is already used: {}", port);
        } catch (IOException e) {
            logger.error("Got an IOException: {}", e);
        }


    }


    private void handleRequest(PrintStream socketOutput, Set<Piece> pieces, Report report) {
        if (!report.hasErrors() && !pieces.isEmpty()) {
            PuzzleValidator puzzleValidator = new PuzzleValidator(report, pieces);
            List<int[]> options = puzzleValidator.getOptions();
            if (!report.hasErrors() && !options.isEmpty()) {
                Solver solver = new Solver(report, pieces);
                solvePuzzle(solver, options);
            }
        }
        OutputHandler outputHandler = new OutputHandler(report);
        outputHandler.reportJsonToSocket(socketOutput);
    }

    private void solvePuzzle(Solver solver, List<int[]> options) {
        options.stream()
                .peek(option -> logger.debug("Current being handled option: " + Arrays.toString(option)))
                .filter(solver::findSolution)
                .findFirst();
    }

    private static void exitWithError(String error) {
        logger.fatal(error);
        System.exit(1);
    }
}


