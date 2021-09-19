import java.io.IOException;
import java.util.*;

public class Lab1 {

    /**
     * G = {01; SZ, A; S -> 00S | 11S | 01A | 10A | !, A -> 00A | 11A | 01S | 10S; S}
     */
    private static String grammar = "G = {010; S, A; S -> 00S | 11S | 01A | 10A | !, A -> 00A | 11A | 01S | 10S; S}";
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

    public static Grammar parseGrammar(char[] terminals, char[] nonTerminals, String[] rules, char startNonTerminal) throws Exception {
        grammar = grammar.replace(" ", "");
        grammar = grammar.replace("{", "");
        grammar = grammar.replace("}", "");
        grammar = grammar.substring(2);

        String[] grammarMembers = grammar.split(";");
        if (grammarMembers.length != 4) {
            System.err.println("Wrong grammar format!\nMust be like: \"{VT; VN; P; S}\"");
            throw new Exception();
        }

        terminals = grammarMembers[0].toCharArray();
        String[] stringNonTerminals = grammarMembers[1].split(",");
        rules = grammarMembers[2].split(",");
        String stringStartNonTerminal = grammarMembers[3];

        terminals = validateTerminals(terminals);
        nonTerminals = validateNonTerminals(stringNonTerminals);
        startNonTerminal = validateStartNonTerminal(stringStartNonTerminal, nonTerminals);
        validateRules(rules, terminals, nonTerminals);

        //System.out.println(Arrays.toString(grammarMembers));
        return new Grammar(terminals, nonTerminals, rules, startNonTerminal);
    }

    /**
     * Переводим в нижний регистр и удаляем повторения
     */
    public static char[] validateTerminals(char[] terminals) throws Exception {
        checkForInvalidSymbolsInTerminalArray(terminals);
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

    public static void checkForInvalidSymbolsInTerminalArray(char[] terminals) throws Exception {
        for (int i = 0; i < terminals.length; i++) {
            if (terminals[i] == ',' || terminals[i] == '|') {
                System.err.println("Invalid terminal symbol: \",\" or \"|\"!");
                throw new Exception();
            }
        }
    }

    /**
     * Переводим в верхний регистр и удаляем повторения, проверяем на наличие цифр,
     * проверяем чтобы нетерминал состоял из одного символа, записываем в массив типа char
     */
    public static char[] validateNonTerminals(String[] nonTerminals) throws Exception {
        TreeSet<String> nonTerminalSet = new TreeSet<>();
        for (String someNonTerminal : nonTerminals) {
            if (someNonTerminal.length() != 1) {
                System.err.println("NonTerminal must contains only one symbol");
                throw new Exception();
            }
            if (Character.isDigit(someNonTerminal.charAt(0))) {
                System.err.println("NonTerminal can't be a numeric symbol");
                throw new Exception();
            }
            if (someNonTerminal.charAt(0) == '|') {
                System.err.println("NonTerminal can't be a \"|\" symbol");
                throw new Exception();
            }

            nonTerminalSet.add(someNonTerminal.toUpperCase());
        }
        //System.out.println(nonTerminalSet.size());
        String[] tempArr = new String[nonTerminalSet.size()];
        for (int i = 0; i < tempArr.length; i++) {
            tempArr[i] = nonTerminalSet.pollFirst();
        }

        char[] newNonTerminalsArray = new char[tempArr.length];
        for (int i = 0; i < tempArr.length; i++) {
            newNonTerminalsArray[i] = tempArr[i].charAt(0);
        }
        return newNonTerminalsArray;
    }

    /**
     * Переводим в верхний регистр и проверяем на наличие в списке нетерминальных символов
     */
    public static char validateStartNonTerminal(String startRule, char[] nonTerminals) throws Exception {
        startRule = startRule.toUpperCase();
        boolean isFound = false;
        for (int i = 0; i < nonTerminals.length; i++) {
            if (nonTerminals[i] == startRule.charAt(0)) {
                isFound = true;
                break;
            }
        }
        if (!isFound) {
            System.err.println("Unknown nonTerminal symbol used as start nonTerminal");
            throw new Exception();
        }

        return startRule.charAt(0);
    }

    public static void validateRules(String[] rules, char[] terminals, char[] nonTerminals) throws Exception {
        for (String rule : rules) {
            if (!Character.isUpperCase(rule.charAt(0))) {
                System.err.println("NonTerminal must be in upper case");
                throw new Exception();
            }

            if (rule.charAt(1) != '-' || rule.charAt(2) != '>') {
                System.err.println("Rule must contain \"->\"");
                throw new Exception();
            }

            for (int i = 3; i < rule.length(); i++) {
                if (Character.isDigit(rule.charAt(i)) && !isElementInArray(rule.charAt(i), terminals)) {
                    System.err.println("Unknown numeric Terminal in rule!");
                    throw new Exception();
                }
                if (Character.isUpperCase(rule.charAt(i)) && !isElementInArray(rule.charAt(i), nonTerminals)) {
                    System.err.println("Unknown NonTerminal in rule!");
                    throw new Exception();
                }
                if (rule.charAt(i) != '!' && rule.charAt(i) != '|' &&
                        Character.isLowerCase(rule.charAt(i)) && !isElementInArray(rule.charAt(i), terminals)) {
                    System.err.println("Unknown Terminal in rule!");
                    throw new Exception();
                }
            }
            if (rule.charAt(rule.length() - 1) == '|') {
                System.err.println("Unfinished rule ended by symbol '|'!");
                throw new Exception();
            }
        }
    }

    public static boolean isElementInArray(char element, char[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == element) return true;
        }
        return false;
    }

    public static void generateLanguageSequences() {
    }

    public static void main(String[] args) {
        /*try {
            inputData();
        } catch (IOException e) {
            System.err.println("Input data error!");
            return;
            //e.printStackTrace();
        }*/

        char[] terminals = new char[0];
        char[] nonTerminals = new char[0];
        String[] rules = new String[0];
        char startNonTerminal = 'a';
        Grammar currentGrammar;
        try {
            currentGrammar = parseGrammar(terminals, nonTerminals, rules, startNonTerminal);
        } catch (Exception e) {
            System.err.println("Grammar parsing Exception!");
            return;
            //e.printStackTrace();
        }

        System.out.println(currentGrammar);
    }
}
