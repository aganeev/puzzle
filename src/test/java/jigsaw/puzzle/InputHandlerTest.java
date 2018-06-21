package jigsaw.puzzle;
import jigsaw.puzzle.entities.Report;
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

    @ParameterizedTest
    @DisplayName("Invalid input - missing pieces")
    @MethodSource("invalidInputMissingPieces")
    void invalidInputMissingPieces(String input, String expectedResult) {
        String PATH = "src/test/resources/";
        String path = PATH + input;
        Report report = new Report();
        InputHandler inputHandler = new InputHandler(report);
        inputHandler.readFromFile(path);
        assertEquals(1, report.getRemarks().size());
        assertEquals(expectedResult, report.getRemarks().get(0));
    }

    private static Stream<Arguments> invalidInputMissingPieces() {
        return Stream.of(Arguments.of("invalidInputMissingPieces(1).txt", "Missing puzzle element(s) with the following IDs: 11"),
                Arguments.of("invalidInputMissingPieces(2).txt", "Missing puzzle element(s) with the following IDs: 8,11"));
    }

    @Test
    @DisplayName("Invalid input - wrong ID")
    void invalidInputWrongID() {
        String INPUT = "invalidInputWrongID(1).txt";
        String PATH = "src/test/resources/";
        String path = PATH + INPUT;
        Report report = new Report();
        InputHandler inputHandler = new InputHandler(report);
        inputHandler.readFromFile(path);
        String expectedErrorMessage = "Puzzle of size 12 cannot have the following ID(s): 13";
        assertEquals(1, report.getRemarks().size());
        assertEquals(expectedErrorMessage, report.getRemarks().get(0));
    }

    @ParameterizedTest
    @DisplayName("Invalid input - wrong piece edges")
    @MethodSource("invalidInputWrongPieceEdges")
    void invalidInputWrongPieceEdges(String input, String expectedResult) {
        String PATH = "src/test/resources/";
        String path = PATH + input;
        Report report = new Report();
        InputHandler inputHandler = new InputHandler(report);
        inputHandler.readFromFile(path);
        assertTrue(report.getRemarks().contains(expectedResult));
    }

    private static Stream<Arguments> invalidInputWrongPieceEdges() {
        return Stream.of(Arguments.of("invalidInputWrongPieceEdges(1).txt", "Puzzle ID 1 has wrong data: 11 1 0 -1 -1 1"),
                Arguments.of("invalidInputWrongPieceEdges(2).txt", "Puzzle ID 6 has wrong data: 6 1 1 1"),
                Arguments.of("invalidInputWrongPieceEdges(3).txt", "Puzzle ID 2 has wrong data: 2 1 0 -1 7"));
    }

    @Test
    @DisplayName("File not found exception")
    void fileNotFound(){
        String file = "";
        String PATH = "src/test/resources/";
        String path = PATH + file;
        Report report = new Report();
        InputHandler inputHandler = new InputHandler(report);
        inputHandler.readFromFile(path);
        assertTrue(report.getRemarks().contains("Error::File Not Found"));
    }


    // TODO: fix the "contains" error message.
    @Test
    @Disabled
    @DisplayName("Valid Input - not solving the puzzle")
    void validInputNoSolving() throws IOException {
        String fileInput = "validInputNoSolving(1).txt";
        String PATH = "src/test/resources/";
        String path = PATH + fileInput;
        Report report = new Report();
        assertTrue(report.getRemarks().contains("Cannot solve puzzle"));
    }

    /*****************************************************************
     TODO: send solving puzzle file, make sure only 1 line appears:
    Cannot solve puzzle: it seems that there is no proper solution
    *****************************************************************/
}