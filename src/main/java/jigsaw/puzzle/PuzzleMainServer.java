package jigsaw.puzzle;

import jigsaw.puzzle.entities.Piece;
import jigsaw.puzzle.entities.Report;
import jigsaw.puzzle.entities.PieceSet;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Logger;
import java.util.logging.Level;
import com.google.gson.Gson;

public class PuzzleMainServer {
    static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    static ServerSocket server = null;
    static Socket socket = null;


    public static void main(String[] args) {

        logger.setLevel(Level.INFO);

        PuzzleMainServer puzzleMainServer = new PuzzleMainServer();

        long startTime = System.nanoTime();
        String outputPath = "C:\\Users\\od104b\\IdeaProjects\\puzzle\\src\\test\\resources\\output.txt";

        try (ServerSocket listener = new ServerSocket(7095)) {

            logger.info("Server is up...");

            Socket socket = listener.accept();
            BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
            PrintStream socketOutput = new PrintStream(socket.getOutputStream(), /*autoflush*/ true, "UTF8");
            String line = "";

            while (true) {
                line = socketInput.readLine();
                Gson gson = new Gson();
                PieceSet pieces = gson.fromJson(line, PieceSet.class);
                for (Piece p: pieces.getPiece()) {
                    System.out.println(p.getId());
                }

            }



        } catch (IOException e) {
            logger.log(Level.SEVERE, "IOException error...");
        }


    }



    /*    PuzzleMainServer puzzleMain = new PuzzleMainServer();
        puzzleMain.doWork(inputPath, outputPath);
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Elapsed time: " + TimeUnit.NANOSECONDS.toSeconds(estimatedTime) + " seconds"); */


    private void doWork(String inputPath, String outputPath) {
        Report report = new Report();
        InputHandler inputHandler = new InputHandler(report);
        OutputHandler outputHandler = new OutputHandler(report);
        Set<Piece> pieces = inputHandler.readFromFile(inputPath);

        if (!report.hasErrors() && !pieces.isEmpty()) {
            PuzzleValidator puzzleValidator = new PuzzleValidator(report, pieces);
            Set<int[]> options = puzzleValidator.getOptions();
            System.out.println("All side options:");
            options.forEach(option -> System.out.println(Arrays.toString(option)));
            if (!report.hasErrors() && !options.isEmpty()) {
                Solver solver = new Solver(report, pieces);
                if (options.stream()
                        .peek(option -> System.out.println("Current being handled option: " + Arrays.toString(option)))
                        .noneMatch(solver::hasSolution)) {
                    report.addErrorLine("Cannot solve puzzle: it seems that there is no proper solution");
                }
            }
        }
        System.out.println("Done");
        outputHandler.reportToFile(outputPath);
    }

}
