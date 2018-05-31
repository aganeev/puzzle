package jigsaw.puzzle.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Report {
    private List<String> errors;
    private int[] solution = new int[]{};
    private AtomicBoolean hasSolution = new AtomicBoolean(false);

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
        return hasSolution.get();
    }

    public void setSolution(int[] solution) {
        if (hasSolution.compareAndSet(false,true)) {
            this.solution = solution;
        }
    }

    public List<String> getErrors() {
        return errors;
    }

    public int[] getSolution() {
        return solution;
    }
}
