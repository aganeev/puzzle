package jigsaw.puzzle.entities;

import java.util.ArrayList;
import java.util.List;

public class Report {
    private List<String> remarks;
    private int[] solution = new int[0];

    public Report() {
        this.remarks = new ArrayList<>();
    }

    public void addLine(String line) {
        remarks.add(line);
    }

    public boolean hasErrors(){
        return !remarks.isEmpty();
    }

    public boolean hasSolution(){
        return solution.length > 0;
    }

    public void setSolution(int[] solution) {
        this.solution = solution;
    }

    public List<String> getRemarks() {
        return remarks;
    }

    public int[] getSolution() {
        return solution;
    }
}
