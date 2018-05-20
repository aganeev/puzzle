package jigsaw.puzzle;

import jigsaw.puzzle.entities.Piece;
import jigsaw.puzzle.entities.Report;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

class InputHandler {
    private InputHandler(){}

    static Set<Piece> readFromFile(Report report, File file) {
        /* TODO File reading and validation:
         1) File exist
         2) readable
         3) Has at least two lines
         4) The first line is NumElements=%int
         etc...
         everything that can be done here

         each error should be added to the report like report.addErrorLine(errorString);

         pieces array should be returned even if there are errors
         If it's impossible to return appropriate array, an empty array should be returned.
         */

        Set<Piece> retValue = new HashSet<>();
        retValue.add(new Piece(1, new int[]{0,0,0,0}));
        retValue.add(new Piece(2, new int[]{0,0,0,0}));
        retValue.add(new Piece(3, new int[]{0,0,0,0}));
        retValue.add(new Piece(4, new int[]{0,0,0,0}));
        retValue.add(new Piece(5, new int[]{0,0,0,0}));
        retValue.add(new Piece(6, new int[]{0,0,0,0}));
        retValue.add(new Piece(7, new int[]{0,0,0,0}));
        retValue.add(new Piece(8, new int[]{0,0,0,0}));

        return retValue;
    }
}
