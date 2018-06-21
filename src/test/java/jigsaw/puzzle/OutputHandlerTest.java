package jigsaw.puzzle;

import jigsaw.puzzle.entities.Report;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

class OutputHandlerTest {
    private Report report;
    private Scanner fileScanner;
    private OutputHandler outputHandler;

    private final static String OUTPUT_FILE_PATH = "target/output.txt";
    private final static File OUTPUT_FILE = new File(OUTPUT_FILE_PATH);
    private final static ByteArrayOutputStream OUT_CONTENT = new ByteArrayOutputStream();
    private final static ByteArrayOutputStream ERR_CONTENT = new ByteArrayOutputStream();


    @BeforeAll
    static void setUpStreams() {
        System.setOut(new PrintStream(OUT_CONTENT));
        System.setErr(new PrintStream(ERR_CONTENT));
    }

    @AfterAll
    static void restoreStreams() {
        System.setOut(System.out);
        System.setErr(System.err);
    }

    @BeforeEach
    void initEachTest() throws IOException {
        report = new Report();
        if (!OUTPUT_FILE.exists()) {
            OUTPUT_FILE.createNewFile();
        }
        fileScanner = new Scanner(OUTPUT_FILE);
        outputHandler = new OutputHandler(report, OUTPUT_FILE_PATH);
        OUT_CONTENT.reset();
        ERR_CONTENT.reset();
    }

    @Test
    void emptyReport(){
        String expectedError = "Cannot solve puzzle: it seems that there is no proper solution";
        outputHandler.reportToFile();
        assertEquals(expectedError, fileScanner.nextLine());
        assertTrue(OUT_CONTENT.toString().isEmpty());
        assertTrue(ERR_CONTENT.toString().isEmpty());
    }

    @Test
    void reportHasOneError(){
        String expectedError = "This is error line";
        report.addLine(expectedError);
        outputHandler.reportToFile();
        assertEquals(expectedError, fileScanner.nextLine());
        assertFalse(fileScanner.hasNextLine());
        assertTrue(OUT_CONTENT.toString().isEmpty());
        assertTrue(ERR_CONTENT.toString().isEmpty());
    }

    @Test
    void reportHasSeveralErrors(){
        String expectedError1 = "This is error line";
        String expectedError2 = "This is error line number two";
        String expectedError3 = "This is error line number three";
        report.addLine(expectedError1);
        report.addLine(expectedError2);
        report.addLine(expectedError3);
        outputHandler.reportToFile();
        assertEquals(expectedError1, fileScanner.nextLine());
        assertEquals(expectedError2, fileScanner.nextLine());
        assertEquals(expectedError3, fileScanner.nextLine());
        assertFalse(fileScanner.hasNextLine());
        assertTrue(OUT_CONTENT.toString().isEmpty());
        assertTrue(ERR_CONTENT.toString().isEmpty());
    }

    @Test
    void reportHasSolution(){
        int numbersInLine = 4;
        int numberOfLines = 3;
        int[] solution = generateRandomNumbers(numbersInLine, numberOfLines);
        report.setSolution(solution);
        outputHandler.reportToFile();
        List<String> expectedStrings = new ArrayList<>();
        for (int j = 0; j < numberOfLines; j++) {
            StringBuilder string = new StringBuilder();
            for (int i = numbersInLine*j + 1; i <= numbersInLine*(j+1); i++) {
                string.append(solution[i]).append(" ");
            }
            expectedStrings.add(string.toString());
        }
        expectedStrings.forEach(string->assertEquals(string, fileScanner.nextLine()));
        assertFalse(fileScanner.hasNextLine());
        assertTrue(OUT_CONTENT.toString().isEmpty());
        assertTrue(ERR_CONTENT.toString().isEmpty());
    }

    private int[] generateRandomNumbers(int numbersInLine, int numberOfLines) {
        int numberOfIntsToGenerate = numberOfLines*numbersInLine;
        int[] retVal = new int[numberOfIntsToGenerate + 1];
        retVal[0] = numberOfLines;
        for (int i = 1; i <= numberOfIntsToGenerate; i++) {
            retVal[i] = ThreadLocalRandom.current().nextInt(1, 100);
        }
        return retVal;
    }
}