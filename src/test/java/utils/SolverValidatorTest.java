package utils;

import jigsaw.puzzle.entities.Piece;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import utils.SolverValidator;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SolverValidatorTest {
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
    void initEachTest() {
        OUT_CONTENT.reset();
        ERR_CONTENT.reset();
    }

    private static Stream<Arguments> testData() {
        return Stream.of(
                Arguments.of(
                        Stream.of(new Piece(1, new int[]{0, 0, 1, 1}),
                                new Piece(2, new int[]{-1, 0, 0, 1}),
                                new Piece(3, new int[]{0, -1, 1, 0}),
                                new Piece(4, new int[]{-1, -1, 0, 0})),
                        new int[]{1,2,3,4},
                        "Wrong solution length",
                        "solution less than piece set + 1"),
                Arguments.of(
                        Stream.of(new Piece(1,new int[]{0,0,1,1}),
                                new Piece(2,new int[]{-1,0,0,1}),
                                new Piece(3,new int[]{0,-1,1,0}),
                                new Piece(4,new int[]{-1,-1,0,0})),
                        new int[]{1,2,3,4,5,6},
                        "Wrong solution length",
                        "solution more than piece set + 1"),
                Arguments.of(
                        Stream.of(new Piece(1,new int[]{0,0,1,1}),
                                new Piece(2,new int[]{0,0,0,1}),
                                new Piece(3,new int[]{0,-1,1,0}),
                                new Piece(4,new int[]{-1,-1,0,0})),
                        new int[]{2,1,2,3,4},
                        "Piece with id=2 is not match with left piece.",
                        "not match inside row"),
                Arguments.of(
                        Stream.of(new Piece(1,new int[]{0,0,1,1}),
                                new Piece(2,new int[]{-1,0,1,1}),
                                new Piece(3,new int[]{0,-1,1,0}),
                                new Piece(4,new int[]{-1,-1,0,0})),
                        new int[]{2,1,2,3,4},
                        "Last piece in the row number 1 is not match with border.",
                        "not match end of row"),
                Arguments.of(
                        Stream.of(new Piece(1,new int[]{0,0,1,1}),
                                new Piece(2,new int[]{-1,0,0,0}),
                                new Piece(3,new int[]{0,-1,1,0}),
                                new Piece(4,new int[]{-1,-1,0,0})),
                        new int[]{2,1,2,3,4},
                        "Piece with id=4 is not match with top piece.",
                        "not match inside column"),
                Arguments.of(
                        Stream.of(new Piece(1,new int[]{0,0,1,1}),
                                new Piece(2,new int[]{-1,0,0,1}),
                                new Piece(3,new int[]{0,-1,1,0}),
                                new Piece(4,new int[]{-1,-1,0,-1})),
                        new int[]{2,1,2,3,4},
                        "Last piece in the column number 2 is not match with border.",
                        "not match end of column"));
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("testData")
    void solutionLessThanPieceSetPlusOne(Stream<Piece> pieces, int[] solution, String expectedError, String testName){
        Set<Piece> pieceSet = new HashSet<>();
        pieces.forEach(pieceSet::add);
        assertFalse(SolverValidator.isSolutionValid(pieceSet,solution));
        assertEquals(expectedError, ERR_CONTENT.toString().trim());
    }
}
