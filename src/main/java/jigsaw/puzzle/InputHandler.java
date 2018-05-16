package jigsaw.puzzle;

import jigsaw.puzzle.entities.Report;

import java.io.File;

class InputHandler {
    private InputHandler(){}

    static int[][] readFromFile(Report report, File file) {
        /* TODO File reading and validation:
         1) File exist
         2) readable
         3) Has at least two lines
         4) The first line is NumElements=%int
         etc...
         everything that can be done here

         each error should be added to the report like report.addErrorLine(errorString);

         pieces array should returned even if there are errors
         If it's impossible to return appropriate array, an empty array should be returned.
         */

        return new int[][]{{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}};
    }
}
