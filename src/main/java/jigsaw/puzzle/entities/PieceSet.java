package jigsaw.puzzle.entities;

import java.util.Set;

public class PieceSet {

    private Set<Piece> pieces;

    public PieceSet(Set<Piece> pieces) {
        this.pieces = pieces;
    }

    public Set<Piece> getPieces() {
        return pieces;
    }
}
