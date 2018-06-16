package jigsaw.puzzle;

import jigsaw.puzzle.entities.Piece;
import jigsaw.puzzle.entities.Report;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PuzzleMainServer {
    private static final Logger logger = LogManager.getLogger(PuzzleMainServer.class.getName());


    public static void main(String[] args) {
        PuzzleMainServer puzzleMainServer = new PuzzleMainServer();
        puzzleMainServer.start(2, 7095);
    }

    private void start(int numOfThreads, int port) {

        try (ServerSocket listener = new ServerSocket(port)) {
            logger.info("Server is up...");
            ForkJoinPool myPool = new ForkJoinPool(numOfThreads);
            while (!listener.isClosed()) {
                Socket socket = listener.accept();
                myPool.execute(()-> {
                    try {
                        handleRequest(socket);
                    } catch (IOException e) {
                        logger.error("Got an IOException during request handling: {}", e);
                    }
                });
            }
        } catch (IOException e) {
            logger.error("Got an IOException: {}", e);
        }


    }

    private void handleRequest(Socket socket) throws IOException {
        Report report = new Report();
        InputHandler inputHandler = new InputHandler(report);
        OutputHandler outputHandler = new OutputHandler(report);
        try (BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
        PrintStream socketOutput = new PrintStream(socket.getOutputStream(),true, "UTF8"))
        {
            String request = socketInput.readLine();
            logger.debug(request);
            Set<Piece> pieces = inputHandler.readFromJson(request);
            if (!report.hasErrors() && !pieces.isEmpty()) {
                PuzzleValidator puzzleValidator = new PuzzleValidator(report, pieces);
                Set<int[]> options = puzzleValidator.getOptions();
                if (!report.hasErrors() && !options.isEmpty()) {
                    Solver solver = new Solver(report, pieces);
                    solvePuzzle(solver, options);
                }
            }
            outputHandler.reportJsonToSocket(socketOutput);
        } finally {
            socket.close();
        }
    }

    private String getStringRequestFromSocket(BufferedReader socketInput) throws IOException {
        StringBuilder request = new StringBuilder();
        String line;
        while ((line = socketInput.readLine()) != null && !line.equals("")) {
            request.append(line);
        }
        return request.toString();
    }


    private void solvePuzzle(Solver solver, Set<int[]> options) {
        options.stream()
                .peek(option -> logger.debug("Current being handled option: " + Arrays.toString(option)))
                .filter(solver::findSolution)
                .findFirst();
    }

}


