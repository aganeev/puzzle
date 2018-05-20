package jigsaw.puzzle.entities;

import java.util.Objects;
import java.util.stream.IntStream;

public class Piece {
    private int id;
    private int[] edges;
    private int sumOfEdges;

    private static final int TOP = 0;
    private static final int RIGHT = 1;
    private static final int BOTTOM = 2;
    private static final int LEFT = 3;

    public Piece(int id, int[] edges) {
        this.id = id;
        this.edges = edges;
        this.sumOfEdges = IntStream.of(edges).sum();
    }

    public int getId() {
        return id;
    }

    public int getTop(){
        return edges[TOP];
    }

    public int getRight(){
        return edges[RIGHT];
    }

    public int getBottom(){
        return edges[BOTTOM];
    }

    public int getLeft(){
        return edges[LEFT];
    }

    public int getSumOfEdges() {
        return sumOfEdges;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Piece)) return false;
        Piece piece = (Piece) o;
        return id == piece.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
