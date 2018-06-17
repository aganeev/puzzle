package jigsaw.puzzle.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

public class Piece {
    private int id;
    @SerializedName("piece")
    private int[] edges;
    @Expose
    private Integer sumOfEdges;

    private static final int LEFT = 0;
    private static final int TOP = 1;
    private static final int RIGHT = 2;
    private static final int BOTTOM = 3;

    public Piece(int id, int[] edges) {
        this.id = id;
        this.edges = edges;
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
        if (sumOfEdges == null) {
            sumOfEdges = IntStream.of(edges).sum();
        }
        return sumOfEdges;
    }

    public int[] getEdges() {
        return edges;
    }

    public String fullPieceToString(){
        return String.format("{%s: %s}", id, Arrays.toString(edges));
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
