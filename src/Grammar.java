import java.util.Arrays;

public class Grammar {
    char[] terminals;
    char[] nonTerminals;
    String[] rules;
    char startNonTerminal;

    public Grammar(char[] terminals, char[] nonTerminals, String[] rules, char startNonTerminal) {
        this.terminals = terminals;
        this.nonTerminals = nonTerminals;
        this.rules = rules;
        this.startNonTerminal = startNonTerminal;
    }

    public char[] getTerminals() {
        return terminals;
    }

    public void setTerminals(char[] terminals) {
        this.terminals = terminals;
    }

    public char[] getNonTerminals() {
        return nonTerminals;
    }

    public void setNonTerminals(char[] nonTerminals) {
        this.nonTerminals = nonTerminals;
    }

    public String[] getRules() {
        return rules;
    }

    public void setRules(String[] rules) {
        this.rules = rules;
    }

    public char getStartNonTerminal() {
        return startNonTerminal;
    }

    public void setStartNonTerminal(char startNonTerminal) {
        this.startNonTerminal = startNonTerminal;
    }

    @Override
    public String toString() {
        return "Grammar{" +
                "terminals=" + Arrays.toString(terminals) +
                ", nonTerminals=" + Arrays.toString(nonTerminals) +
                ", rules=" + Arrays.toString(rules) +
                ", startNonTerminal=" + startNonTerminal +
                '}';
    }
}
