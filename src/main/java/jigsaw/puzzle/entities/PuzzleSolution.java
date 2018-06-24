package jigsaw.puzzle.entities;

public class PuzzleSolution {
    private boolean solutionExists;
    private String[] errors;
    private Solution solution;

    public boolean isSolutionExists() {
        return solutionExists;
    }

    public void setSolutionExists(boolean solutionExists) {
        this.solutionExists = solutionExists;
    }

    public String[] getErrors() {
        return errors;
    }

    public void setErrors(String[] errors) {
        this.errors = errors;
    }

    public Solution getSolution() {
        return solution;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }
}
