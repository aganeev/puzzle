package jigsaw.puzzle.entities;

public class Report {
    private String text;

    public Report() {
        this.text = "";
    }

    public void addErrorLine(String line) {
        text += "\n" + line;
    }

    public boolean isEmpty(){
        return text.isEmpty();
    }

    public void setSucceedResult(String result) {
        text = result;
    }

    @Override
    public String toString() {
        return text;
    }
}
