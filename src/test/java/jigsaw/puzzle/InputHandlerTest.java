package jigsaw.puzzle;
import jigsaw.puzzle.entities.Piece;
import jigsaw.puzzle.entities.Report;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

class InputHandlerTest {
    private static final String PATH = "src/test/resources/";
    private Report report;
    private InputHandler inputHandler;

    private final static ByteArrayOutputStream OUT_CONTENT = new ByteArrayOutputStream();
    private final static ByteArrayOutputStream ERR_CONTENT = new ByteArrayOutputStream();

    @BeforeEach
    void initTest(){
        report = new Report();
        inputHandler = new InputHandler(report);
        OUT_CONTENT.reset();
        ERR_CONTENT.reset();
    }

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

    private static Stream<String> validInputData() {
        return Stream.of("validInputNumElementsWithSpaces.txt",
                "validInputPieceDataWithSpaces.txt",
                "validInputPieceDataWithSpaces2.txt",
                "validInputWithCommentLine.txt",
                "validInputWithEmptyLine.txt");
    }

    private static Stream<Arguments> invalidInputData() {
        return Stream.of(
                Arguments.of(
                        "invalidInputNumElementsError(1).txt",
                        "Error: Parameter 'NumElements' is not found",
                        1,
                        0,
                        "Invalid NumElements parameter"),
                Arguments.of(
                        "invalidInputNumElementsError(2).txt",
                        "Wrong NumElements parameter format. Expected: NumElements=<number>",
                        1,
                        0,
                        "NumElements no value"),
                Arguments.of(
                        "invalidInputNumElementsError(3).txt",
                        "Wrong NumElements parameter format. Expected: NumElements=<number>",
                        1,
                        0,
                        "NumElements no equality sign"),
                Arguments.of(
                        "invalidInputNumElementsError(4).txt",
                        "Wrong NumElements parameter value (a). Expected integer",
                        1,
                        0,
                        "NumElements not integer"),
                Arguments.of(
                        "invalidInputMissingPieces(1).txt",
                        "Missing puzzle element(s) with the following ID(s): 11",
                        1,
                        0,
                        "Missing one piece"),
                Arguments.of(
                        "invalidInputMissingPieces(2).txt",
                        "Missing puzzle element(s) with the following ID(s): 8, 11",
                        1,
                        0,
                        "Missing two pieces"),
                Arguments.of(
                        "invalidInputWrongID(1).txt",
                        "Puzzle of size 12 cannot have the following ID(s): 13",
                        1,
                        0,
                        "Wrong ID more than size"),
                Arguments.of(
                        "invalidInputWrongID(2).txt",
                        "Wrong Puzzle ID value. Cannot be 0",
                        1,
                        0,
                        "Wrong ID '0'"),
                Arguments.of(
                        "invalidInputWrongID(3).txt",
                        "Wrong Puzzle ID value (q). Expected: integer",
                        1,
                        0,
                        "Wrong ID - String"),
                Arguments.of(
                        "invalidInputWrongID(4).txt",
                        "Puzzle ID 6 has specified more than one time",
                        1,
                        0,
                        "Wrong ID - duplicated"),
                Arguments.of(
                        "invalidInputWrongPieceEdges(1).txt",
                        "Puzzle ID 11 has wrong data: 11 1 0 -1 -1 1",
                        2,
                        0,
                        "More than 4 edges"),
                Arguments.of(
                        "invalidInputWrongPieceEdges(2).txt",
                        "Puzzle ID 6 has wrong data: 6 1 1 1",
                        2,
                        0,
                        "Less than 4 edges"),
                Arguments.of(
                        "invalidInputWrongPieceEdges(3).txt",
                        "Puzzle ID 2 has wrong data: 2 1 0 -1 7",
                        2,
                        0,
                        "Wrong edge value - 7"),
                Arguments.of(
                        "invalidInputWrongPieceEdges(4).txt",
                        "Puzzle ID 6 has wrong data: 6 a 1 1 0",
                        2,
                        0,
                        "Wrong edge value - String"));
    }

    @ParameterizedTest
    @MethodSource("validInputData")
    void validDataTest(String input) {
        String path = PATH + input;
        Set<Piece> pieceSet = inputHandler.readFromFile(path);
        assertAll(
                ()->assertFalse(report.hasErrors()),
                ()->assertEquals(0, report.getErrors().size()),
                ()->assertEquals(12, pieceSet.size())
        );
    }

    @ParameterizedTest(name = "{4}")
    @MethodSource("invalidInputData")
    void invalidDataTest(String input, String expectedResult, int numOfErrors, int errorIndex, String name) {
        String path = PATH + input;
        inputHandler.readFromFile(path);
        assertAll(
                ()->assertEquals(numOfErrors, report.getErrors().size()),
                ()->assertEquals(expectedResult, report.getErrors().get(errorIndex))
        );

    }

    @Test
    @DisplayName("Many errors together")
    void invalidDataTest() {
        String path = PATH + "invalidInputManyErrors.txt";
        inputHandler.readFromFile(path);
        List<String> expectedErrorList = new ArrayList<>();
        expectedErrorList.add("Missing puzzle element(s) with the following ID(s): 1, 2, 3, 5, 6, 8, 12");
        expectedErrorList.add("Puzzle of size 12 cannot have the following ID(s): 13, 21");
        expectedErrorList.add("Wrong Puzzle ID value (q). Expected: integer");
        expectedErrorList.add("Puzzle ID 2 has wrong data: 2 1 0 -1 0 1");
        expectedErrorList.add("Puzzle ID 12 has wrong data: 12 0 -1 1");
        expectedErrorList.add("Puzzle ID 3 has wrong data: 3 1 2 0 1");
        expectedErrorList.add("Puzzle ID 6 has wrong data: 6 a 1 1 0");
        expectedErrorList.add("Puzzle ID 5 has wrong data: 5 -1 -1 -1 -2");
        expectedErrorList.add("Wrong Puzzle ID value. Cannot be 0");
        expectedErrorList.add("Puzzle ID 4 has specified more than one time");
        Collections.sort(expectedErrorList);
        List<String> actualErrorList = report.getErrors();
        Collections.sort(actualErrorList);
        assertEquals(expectedErrorList, actualErrorList);
    }

    @Test
    @DisplayName("File not found exception")
    void fileNotFound(){
        String file = "aaa";
        String path = PATH + file;
        inputHandler.readFromFile(path);

        assertTrue(report.getErrors().isEmpty());
        assertEquals(0, report.getSolution().length);
        assertEquals("Error: Input file not found", ERR_CONTENT.toString().trim());
    }


}