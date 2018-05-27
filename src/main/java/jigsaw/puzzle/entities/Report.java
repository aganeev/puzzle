package jigsaw.puzzle.entities;

public class Report {
    private String text;

    public Report() {
        this.text = "";
    }

    public void addErrorLine(String line) {
        text += text.isEmpty() ? line : "\n" + line;
    }

    public boolean isEmpty(){
        return text.isEmpty();
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
