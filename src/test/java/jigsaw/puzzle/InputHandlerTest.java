package jigsaw.puzzle;
import jigsaw.puzzle.entities.Report;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

class InputHandlerTest {
    private static final String PATH = "src/test/resources/";
    private Report report;
    private InputHandler inputHandler;

    @BeforeEach
    void initTest(){
        report = new Report();
        inputHandler = new InputHandler(report);
    }

    private static Stream<Arguments> invalidInputData() {
        return Stream.of(
                Arguments.of(
                        "invalidInputMissingPieces(1).txt",
                        "Missing puzzle element(s) with the following IDs: 11",
                        1,
                        0,
                        "Missing one piece"),
                Arguments.of(
                        "invalidInputMissingPieces(2).txt",
                        "Missing puzzle element(s) with the following IDs: 8,11",
                        1,
                        0,
                        "Missing two pieces"),
                Arguments.of(
                        "invalidInputWrongID(1).txt",
                        "Puzzle of size 12 cannot have the following ID(s): 13",
                        1,
                        0,
                        "Wrong ID"),
                Arguments.of(
                        "invalidInputWrongPieceEdges(1).txt",
                        "Puzzle ID 1 has wrong data: 11 1 0 -1 -1 1",
                        2,
                        1,
                        "More than 4 edges"),
                Arguments.of(
                        "invalidInputWrongPieceEdges(2).txt",
                        "Puzzle ID 6 has wrong data: 6 1 1 1",
                        2,
                        1,
                        "Less than 4 edges"),
                Arguments.of(
                        "invalidInputWrongPieceEdges(3).txt",
                        "Puzzle ID 2 has wrong data: 2 1 0 -1 7",
                        2,
                        1,
                        "Wrong edge value"));
    }

    @ParameterizedTest(name = "{4}")
    @MethodSource("invalidInputData")
    void invalidInputMissingPieces(String input, String expectedResult, int numOfErrors, int errorIndex, String name) {
        String path = PATH + input;
        inputHandler.readFromFile(path);
        assertEquals(numOfErrors, report.getErrors().size());
        assertEquals(expectedResult, report.getErrors().get(errorIndex));
    }

    @Test
    @DisplayName("File not found exception")
    void fileNotFound(){
        String file = "";
        String path = PATH + file;
        inputHandler.readFromFile(path);
        assertTrue(report.getErrors().contains("Error: Input file not found"));
    }

}