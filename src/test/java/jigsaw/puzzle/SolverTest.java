package jigsaw.puzzle;

import jigsaw.puzzle.entities.Piece;
import jigsaw.puzzle.entities.Report;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.SolverValidator;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SolverTest {
    private Set<Piece> pieceSet;
    private Report report;
    private static final int[][] SUCCESS_PIECE_SOURCE = new int[][]{
            {0,-1,1,0},
            {0,1,-1,0},
            {0,0,-1,-1},
            {0,0,1,-1},
            {1,0,0,1},
            {1,-1,0,0},
            {1,0,0,1},
            {1,0,0,0},
            {-1,0,0,0},
            {-1,-1,0,0},
            {0,1,-1,1},
            {0,0,-1,0}};
    private static final List<Integer> IDS = Arrays.asList(12,9,11,7,5,6,4,8,2,10,3,1);

    @BeforeEach
    void initTest() {
        report = new Report();
        Collections.shuffle(IDS);
        Iterator<Integer> idIterator = IDS.iterator();
        pieceSet = new HashSet<>();
        for (int[] edges : SUCCESS_PIECE_SOURCE) {
            pieceSet.add(new Piece(idIterator.next(),edges));
        }
    }

    @Test
    void solverTest4x3(){
        Solver solver = new Solver(report, pieceSet);
        solver.findSolution(new int[]{4,3});
        assertTrue(report.hasSolution());
        assertTrue(SolverValidator.isSolutionValid(pieceSet,report.getSolution()));
    }

    @Test
    void solverTest2x6(){
        Solver solver = new Solver(report, pieceSet);
        solver.findSolution(new int[]{2,6});
        assertTrue(report.hasSolution());
        assertTrue(SolverValidator.isSolutionValid(pieceSet,report.getSolution()));
    }

}