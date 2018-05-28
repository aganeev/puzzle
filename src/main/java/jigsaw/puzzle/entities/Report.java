package jigsaw.puzzle.entities;

import java.util.ArrayList;
import java.util.List;

public class Report {
    private List<String> errors;
    private int[] solution = new int[0];

    public Report() {
        this.errors = new ArrayList<>();
    }

    public void addErrorLine(String line) {
        errors.add(line);
    }

    public boolean hasErrors(){
        return !errors.isEmpty();
    }

    public boolean hasSolution(){
        return solution.length > 0;
    }

    public void setSolution(int[] solution) {
        this.solution = solution;
    }

    public List<String> getErrors() {
        return errors;
    }

    public int[] getSolution() {
        return solution;
    }
}
