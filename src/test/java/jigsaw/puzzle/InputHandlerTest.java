package jigsaw.puzzle;
import jigsaw.puzzle.entities.Piece;
import jigsaw.puzzle.entities.Report;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

class InputHandlerTest {

    @ParameterizedTest
    @DisplayName("Invalid input - missing pieces")
    @MethodSource("invalidInputMissingPieces")
    public void invalidInputMissingPieces(String input, String expectedResult) {
        String PATH = "C:\\Users\\od104b\\IdeaProjects\\puzzle\\src\\test\\resources\\";
        String path = PATH + input;
        Report report = new Report();
        InputHandler.readFromFile(report, path);
        assertEquals(report.toString(), expectedResult);
    }

    static Stream<Arguments> invalidInputMissingPieces() {
        return Stream.of(Arguments.of("invalidInputMissingPieces(1).txt", "Missing puzzle element(s) with the following IDs: 11"),
                Arguments.of("invalidInputMissingPieces(2).txt", "Missing puzzle element(s) with the following IDs: 8,11"));
    }

    @Test
    @DisplayName("Invalid input - wrong ID")
    public void invalidInputWrongID() {
        String INPUT = "invalidInputWrongID(1).txt";
        String PATH = "C:\\Users\\od104b\\IdeaProjects\\puzzle\\src\\test\\resources\\";
        String path = PATH + INPUT;
        Report report = new Report();
        InputHandler.readFromFile(report, path);
        String expectedErrorMessage = "Puzzle of size 12 cannot have the following ID(s): 13";
        assertEquals(report.toString(), expectedErrorMessage);
    }

    @ParameterizedTest
    @DisplayName("Invalid input - wrong piece edges")
    @MethodSource("invalidInputWrongPieceEdges")
    public void invalidInputWrongPieceEdges(String input, String expectedResult) {
        String PATH = "C:\\Users\\od104b\\IdeaProjects\\puzzle\\src\\test\\resources\\";
        String path = PATH + input;
        Report report = new Report();
        InputHandler.readFromFile(report, path);
        assertTrue(report.toString().contains(expectedResult));
    }

    static Stream<Arguments> invalidInputWrongPieceEdges() {
        return Stream.of(Arguments.of("invalidInputWrongPieceEdges(1).txt", "Puzzle ID 1 has wrong data: 11 1 0 -1 -1 1"),
                Arguments.of("invalidInputWrongPieceEdges(2).txt", "Puzzle ID 6 has wrong data: 6 1 1 1"),
                Arguments.of("invalidInputWrongPieceEdges(3).txt", "Puzzle ID 2 has wrong data: 2 1 0 -1 7"));
    }

    @Test
    @DisplayName("File not found exception")
    public void fileNotFound(){
        String file = "";
        String PATH = "C:\\Users\\od104b\\IdeaProjects\\puzzle\\src\\test\\resources\\";
        String path = PATH + file;
        Report report = new Report();
        InputHandler.readFromFile(report, path);
        assertTrue(report.toString().contains("Error::File Not Found"));
    }


    // TODO: fix the "contains" error message.
    @Test
    @Disabled
    @DisplayName("Valid Input - not solving the puzzle")
    public void validInputNoSolving() throws IOException {
        String fileInput = "validInputNoSolving(1).txt";
        String PATH = "C:\\Users\\od104b\\IdeaProjects\\puzzle\\src\\test\\resources\\";
        String path = PATH + fileInput;
        Report report = new Report();
        assertTrue(report.toString().contains("Cannot solve puzzle"));
    }

    /*****************************************************************
     TODO: send solving puzzle file, make sure only 1 line appears:
    Cannot solve puzzle: it seems that there is no proper solution
    *****************************************************************/
}