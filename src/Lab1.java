import java.io.IOException;
import java.util.*;

public class Lab1 {

    /**
     * G = {01; SZ, A; S -> 00S | 11S | 01A | 10A | !, A -> 00A | 11A | 01S | 10S; S}
     */
    private static String grammar = "G = {010; SZ, A; S -> 00S | 11S | 01A | 10A | !, A -> 00A | 11A | 01S | 10S; S}";
    private static boolean outputType; // false - L, true - R
    private static int startLength;
    private static int endLength;

    public static void inputData() throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Если вы хотите использовать грамматику ПО УМОЛЧАНИЮ введите цифру 0, \n" +
                "если вы хотите ввести СВОЮ грамматику введите цифру 1: ");
        int isUserGrammar = scanner.nextInt();
        if (isUserGrammar == 1) {
            System.out.println("Введите грамматику (G = {\"\"; [ , ]; [ , ]; \"\"}): ");
            grammar = scanner.next();
            //System.out.println(inputStr);
        } else if (isUserGrammar != 0) {
            System.err.println("Wrong type of input grammar mode!");
            throw new IOException();
        }

        System.out.print("Введите тип вывода грамматики (левосторонний - L, правосторонний - R): ");
        String outputTypeStr = scanner.next();
        //System.out.println(outputTypeStr);
        if (!outputTypeStr.equals("L") && !outputTypeStr.equals("R")) {
            System.err.println("Wrong type of output grammar type!");
            throw new IOException();
        } else if (outputTypeStr.equals("L")) {
            outputType = false;
        } else {
            outputType = true;
        }

        System.out.print("Введите диапазон длин генерируемых цепочек (start end): ");
        startLength = scanner.nextInt();
        endLength = scanner.nextInt();
        if (startLength < 0 || endLength < 0 || startLength > endLength) {
            System.err.println("Invalid range of generate sequences length!");
            throw new IOException();
        }
        //System.out.println("Start && end: " + startLength + " && " + endLength);

        scanner.close();
    }

    public static void parseGrammar(char[] terminals, String[] nonTerminals, String[] rules, String startRule) throws Exception {
        grammar = grammar.replace(" ","");
        grammar = grammar.replace("{","");
        grammar = grammar.replace("}","");
        grammar = grammar.substring(2);

        String[] grammarMembers = grammar.split(";");

        terminals = grammarMembers[0].toCharArray();
        for (int i = 0; i < terminals.length; i++) {
            if (terminals[i] == ',') {
                System.err.println("Invalid terminal symbol: \",\"!");
                throw new Exception();
            }
        }
        nonTerminals = grammarMembers[1].split(",");
        rules = grammarMembers[2].split(",");
        startRule = grammarMembers[3];

        terminals = validateTerminals(terminals);
        validateRules();

        System.out.println(Arrays.toString(grammarMembers));
    }

    public static char[] validateTerminals(char[] terminals) {
        TreeSet<Character> terminalSet = new TreeSet<>();
        for (char someTerminal : terminals) {
            terminalSet.add(Character.toLowerCase(someTerminal));
        }
        //System.out.println(terminalSet.size());
        char[] tempArr = new char[terminalSet.size()];
        for (int i = 0; i < tempArr.length; i++) {
            tempArr[i] = terminalSet.pollFirst();
        }
        return tempArr;
    }

    public static void validateRules() {

    }

    public static void generateLanguageSequences() {}

    public static void main(String[] args) {
        /*try {
            inputData();
        } catch (IOException e) {
            System.err.println("Input data error!");
            return;
            //e.printStackTrace();
        }*/

        char[] terminals = new char[0];
        String[] nonTerminals = new String[0];
        String[] rules = new String[0];
        String startRule = "";
        try {
            parseGrammar(terminals, nonTerminals, rules, startRule);
        } catch (Exception e) {
            System.err.println("Grammar parsing Exception!");
            //e.printStackTrace();
        }

    }
}
