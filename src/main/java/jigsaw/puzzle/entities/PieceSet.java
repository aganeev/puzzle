package jigsaw.puzzle.entities;

import java.util.Set;

public class PieceSet {

    Set<Piece> piece;

    public Set<Piece> getPiece() {
        return piece;
    }

    public PieceSet(Set<Piece> piece) {
        this.piece = piece;
    }
}
