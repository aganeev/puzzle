package jigsaw.puzzle;

import jigsaw.puzzle.entities.Piece;
import jigsaw.puzzle.entities.Report;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class PuzzleValidatorTest {

    private static Stream<Arguments> successValidationData() {
        return Stream.of(
                Arguments.of(
                        Stream.of(new Piece(1,new int[]{0,0,0,0})),
                        Stream.of(new int[]{1,1}),
                        "One square piece - one option"),
                Arguments.of(
                        Stream.of(
                                new Piece(1,new int[]{0,0,0,0}),
                                new Piece(2,new int[]{0,0,0,0})),
                        Stream.of(
                                new int[]{1,2},
                                new int[]{2,1}),
                        "Two pieces - two options"),
                Arguments.of(
                        Stream.of(
                                new Piece(1,new int[]{0,0,0,0}),
                                new Piece(2,new int[]{0,0,0,0}),
                                new Piece(3,new int[]{0,0,0,0})),
                        Stream.of(
                                new int[]{1,3},
                                new int[]{3,1}),
                        "Three pieces - two options"),
                Arguments.of(
                        Stream.of(
                                new Piece(1,new int[]{0,0,0,0}),
                                new Piece(2,new int[]{0,0,0,0}),
                                new Piece(3,new int[]{0,0,0,0}),
                                new Piece(4,new int[]{0,0,0,0}),
                                new Piece(5,new int[]{0,0,0,0}),
                                new Piece(6,new int[]{0,0,0,0})),
                        Stream.of(
                                new int[]{1,6},
                                new int[]{2,3},
                                new int[]{3,2},
                                new int[]{6,1}),
                        "Six pieces - four options"),
                Arguments.of(
                        Stream.of(
                                new Piece(1,new int[]{0,0,1,1}),
                                new Piece(2,new int[]{-1,0,1,-1}),
                                new Piece(3,new int[]{-1,0,0,1}),
                                new Piece(4,new int[]{0,-1,1,0}),
                                new Piece(5,new int[]{-1,1,1,0}),
                                new Piece(6,new int[]{-1,-1,0,0})),
                        Stream.of(
                                new int[]{3,2}),
                        "Six pieces - one option"),
                Arguments.of(
                        Stream.of(
                                new Piece(1,new int[]{0,0,0,0}),
                                new Piece(2,new int[]{0,0,0,0}),
                                new Piece(3,new int[]{0,0,0,0}),
                                new Piece(4,new int[]{0,0,0,0}),
                                new Piece(5,new int[]{0,0,0,0}),
                                new Piece(6,new int[]{0,0,0,0}),
                                new Piece(7,new int[]{0,0,0,0}),
                                new Piece(8,new int[]{0,0,0,0}),
                                new Piece(9,new int[]{0,0,0,0}),
                                new Piece(10,new int[]{0,0,0,0}),
                                new Piece(11,new int[]{0,0,0,0}),
                                new Piece(12,new int[]{0,0,0,0})),
                        Stream.of(
                                new int[]{1,12},
                                new int[]{2,6},
                                new int[]{3,4},
                                new int[]{4,3},
                                new int[]{6,2},
                                new int[]{12,1}),
                        "Twelfth pieces - six options")
        );
    }

    private static Stream<Arguments> failedValidationData() {
        return Stream.of(
                Arguments.of(
                        Stream.of(new Piece(1, new int[]{0, 0, 0, 1})),
                        Arrays.asList(
                                "Cannot solve puzzle: wrong number of straight edges",
                                "Cannot solve puzzle: missing corner element: BR",
                                "Cannot solve puzzle: missing corner element: BL",
                                "Cannot solve puzzle: sum of edges is not zero"),
                        "One piece - all errors"),
                Arguments.of(
                        Stream.of(
                                new Piece(1,new int[]{0,0,1,0}),
                                new Piece(2,new int[]{-1,1,-1,0}),
                                new Piece(3,new int[]{1,0,0,0})),
                        Arrays.asList(
                                "Cannot solve puzzle: wrong number of straight edges",
                                "Cannot solve puzzle: sum of edges is not zero"),
                        "Three pieces - 'straight edges' and 'sum of edges' errors"),
                Arguments.of(
                        Stream.of(
                                new Piece(1,new int[]{1,0,1,0}),
                                new Piece(2,new int[]{-1,0,0,0}),
                                new Piece(3,new int[]{-1,0,0,0})),
                        Arrays.asList(
                                "Cannot solve puzzle: wrong number of straight edges",
                                "Cannot solve puzzle: missing corner element: TL",
                                "Cannot solve puzzle: missing corner element: BL"),
                        "Three pieces - 'straight edges' and 'corner elements' errors"),
                Arguments.of(
                        Stream.of(
                                new Piece(1,new int[]{0,0,1,0}),
                                new Piece(2,new int[]{1,0,-1,0}),
                                new Piece(3,new int[]{1,0,0,0})),
                        Collections.singletonList("Cannot solve puzzle: sum of edges is not zero"),
                        "Three pieces - 'sum of edges' error"),
                Arguments.of(
                        Stream.of(
                                new Piece(1,new int[]{0,0,1,1}),
                                new Piece(2,new int[]{-1,0,-1,1}),
                                new Piece(3,new int[]{1,0,0,1}),
                                new Piece(4,new int[]{0,-1,-1,0}),
                                new Piece(5,new int[]{-1,0,-1,1}),
                                new Piece(6,new int[]{1,-1,0,0})),
                        Collections.singletonList("Cannot solve puzzle: wrong number of straight edges"),
                        "Six pieces - 'straight edges' error"),
                Arguments.of(
                        Stream.of(
                                new Piece(1,new int[]{1,0,1,1}),
                                new Piece(2,new int[]{-1,0,-1,0}),
                                new Piece(3,new int[]{1,0,0,1}),
                                new Piece(4,new int[]{0,-1,0,0}),
                                new Piece(5,new int[]{0,-1,-1,0}),
                                new Piece(6,new int[]{1,-1,0,0})),
                        Collections.singletonList("Cannot solve puzzle: missing corner element: TL"),
                        "Six pieces - 'corner elements TL' error"),
                Arguments.of(
                        Stream.of(
                                new Piece(1,new int[]{0,0,1,1}),
                                new Piece(2,new int[]{-1,0,-1,1}),
                                new Piece(3,new int[]{1,0,1,-1}),
                                new Piece(4,new int[]{0,-1,1,0}),
                                new Piece(5,new int[]{-1,-1,0,0}),
                                new Piece(6,new int[]{1,-1,0,0})),
                        Collections.singletonList("Cannot solve puzzle: missing corner element: TR"),
                        "Six pieces - 'corner elements TR' error"),
                Arguments.of(
                        Stream.of(
                                new Piece(1,new int[]{0,0,1,1}),
                                new Piece(2,new int[]{-1,0,0,1}),
                                new Piece(3,new int[]{1,0,0,-1}),
                                new Piece(4,new int[]{0,-1,1,0}),
                                new Piece(5,new int[]{-1,-1,-1,0}),
                                new Piece(6,new int[]{1,-1,1,0})),
                        Collections.singletonList("Cannot solve puzzle: missing corner element: BR"),
                        "Six pieces - 'corner elements BR' error"),
                Arguments.of(
                        Stream.of(
                                new Piece(1,new int[]{0,0,1,1}),
                                new Piece(2,new int[]{0,0,-1,1}),
                                new Piece(3,new int[]{1,0,0,1}),
                                new Piece(4,new int[]{-1,-1,1,0}),
                                new Piece(5,new int[]{-1,-1,-1,0}),
                                new Piece(6,new int[]{1,-1,0,0})),
                        Collections.singletonList("Cannot solve puzzle: missing corner element: BL"),
                        "Six pieces - 'corner elements BL' error"),
                Arguments.of(
                        Stream.of(
                                new Piece(1,new int[]{0,0,1,1}),
                                new Piece(2,new int[]{-1,0,0,1}),
                                new Piece(3,new int[]{1,0,0,1}),
                                new Piece(4,new int[]{0,-1,1,0}),
                                new Piece(5,new int[]{-1,-1,-1,0}),
                                new Piece(6,new int[]{1,-1,0,0})),
                        Collections.singletonList("Cannot solve puzzle: sum of edges is not zero"),
                        "Six pieces - 'sum of edges' error (=1)"),
                Arguments.of(
                        Stream.of(
                                new Piece(1,new int[]{0,0,1,1}),
                                new Piece(2,new int[]{-1,0,-1,1}),
                                new Piece(3,new int[]{1,0,0,0}),
                                new Piece(4,new int[]{0,-1,1,0}),
                                new Piece(5,new int[]{-1,-1,-1,0}),
                                new Piece(6,new int[]{1,-1,0,0})),
                        Collections.singletonList("Cannot solve puzzle: sum of edges is not zero"),
                        "Six pieces - 'sum of edges' error (=-1)"),
                Arguments.of(
                        Stream.of(
                                new Piece(1,new int[]{0,0,1,1}),
                                new Piece(2,new int[]{-1,0,-1,1}),
                                new Piece(3,new int[]{1,0,0,1}),
                                new Piece(4,new int[]{0,-1,1,0}),
                                new Piece(5,new int[]{-1,-1,-1,0}),
                                new Piece(6,new int[]{1,-1,1,0})),
                        Arrays.asList(
                                "Cannot solve puzzle: wrong number of straight edges",
                                "Cannot solve puzzle: missing corner element: BR",
                                "Cannot solve puzzle: sum of edges is not zero"),
                        "Six pieces - all errors")
                );
    }


    @ParameterizedTest(name = "{2}")
    @MethodSource("successValidationData")
    void getOptionsSuccessValidationTest(Stream<Piece> piecesStream, Stream<int[]> optionsStream, String caseName) {
        Set<Piece> pieces = createTestSet(piecesStream);
        Report report = new Report();
        PuzzleValidator puzzleValidator = new PuzzleValidator(report, pieces);
        List<int[]> actualOptions = puzzleValidator.getOptions();
        Map<Integer, Integer> actualOptionsMap = actualOptions.stream().collect(Collectors.toMap(option -> option[0], option -> option[1], (a, b) -> b));
        Map<Integer, Integer> optionsMap = optionsStream.collect(Collectors.toSet()).stream().collect(Collectors.toMap(option -> option[0], option -> option[1], (a, b) -> b));
        Map<Integer,Integer> notFound = optionsMap.entrySet().stream().filter(option->!actualOptionsMap.remove(option.getKey(),option.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        assertAll(

                () -> assertTrue(notFound.isEmpty(), "expected, but not received options:" +
                        notFound),
                () -> assertTrue(actualOptionsMap.isEmpty(), "not expected, but received options:" +
                        actualOptionsMap),
                () -> assertTrue(!report.hasErrors())

        );
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("failedValidationData")
    void getOptionsFailedValidationTest(Stream<Piece> piecesStream, List<String> expectedErrors, String caseName) {
        Set<Piece> pieces = createTestSet(piecesStream);
        Report report = new Report();
        PuzzleValidator puzzleValidator = new PuzzleValidator(report, pieces);
        List<int[]> actualOptions = puzzleValidator.getOptions();
        assertAll(
                () -> assertTrue(actualOptions.isEmpty(), "not expected, but received options:" +
                        actualOptions.stream().map(Arrays::toString).collect(Collectors.joining("  "))),
                () -> assertEquals(expectedErrors, report.getErrors())
        );
    }



    private Set<Piece> createTestSet(Stream<Piece> piecesStream) {
        Set<Piece> pieceSet = new HashSet<>();
        piecesStream.forEach(pieceSet::add);
        return pieceSet;
    }
}
