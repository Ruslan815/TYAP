import java.util.Arrays;

public class Grammar {
    private char[] terminals;
    private String[] nonTerminals;
    private String[] rules;
    private String startRule;

    public Grammar(char[] terminals, String[] nonTerminals, String[] rules, String startRule) {
        this.terminals = terminals;
        this.nonTerminals = nonTerminals;
        this.rules = rules;
        this.startRule = startRule;
    }

    public char[] getTerminals() {
        return terminals;
    }

    public void setTerminals(char[] terminals) {
        this.terminals = terminals;
    }

    public String[] getNonTerminals() {
        return nonTerminals;
    }

    public void setNonTerminals(String[] nonTerminals) {
        this.nonTerminals = nonTerminals;
    }

    public String[] getRules() {
        return rules;
    }

    public void setRules(String[] rules) {
        this.rules = rules;
    }

    public String getStartRule() {
        return startRule;
    }

    public void setStartRule(String startRule) {
        this.startRule = startRule;
    }

    @Override
    public String toString() {
        return "Grammar{" +
                "terminals=" + Arrays.toString(terminals) +
                ", nonTerminals=" + Arrays.toString(nonTerminals) +
                ", rules=" + Arrays.toString(rules) +
                ", startRule='" + startRule + '\'' +
                '}';
    }
}
