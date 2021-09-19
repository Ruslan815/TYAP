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

    public static void parseGrammar(char[] terminals, String[] nonTerminals, String[] rules, String startRule) throws Exception {
        grammar = grammar.replace(" ","");
        grammar = grammar.replace("{","");
        grammar = grammar.replace("}","");
        grammar = grammar.substring(2);

        String[] grammarMembers = grammar.split(";");

        terminals = grammarMembers[0].toCharArray();
        nonTerminals = grammarMembers[1].split(",");
        rules = grammarMembers[2].split(",");
        startRule = grammarMembers[3];

        terminals = validateTerminals(terminals);
        nonTerminals = validateNonTerminals(nonTerminals); //TODO Заменить на массив char
        startRule = validateStartRule(startRule, nonTerminals);
        rules = validateRules(rules, terminals, nonTerminals);

//        System.out.println(Arrays.toString(nonTerminals));

        System.out.println(Arrays.toString(grammarMembers));
    }

    /**
     * Переводим в нижний регистр и удаляем повторения
     */
    public static char[] validateTerminals(char[] terminals) throws Exception {
        TreeSet<Character> terminalSet = new TreeSet<>();
        for (char someTerminal : terminals) {
            if(someTerminal == '|' || someTerminal == ',') {
                System.err.println("Terminal character can't be \"|\", \",\"!");
                throw new Exception();
            }
            terminalSet.add(Character.toLowerCase(someTerminal));
        }
        //System.out.println(terminalSet.size());
        char[] tempArr = new char[terminalSet.size()];
        for (int i = 0; i < tempArr.length; i++) {
            tempArr[i] = terminalSet.pollFirst();
        }
        return tempArr;
    }

    /**
     * Переводим в верхний регистр и удаляем повторения
     */
    public static String[] validateNonTerminals(String[] nonTerminals) throws Exception {
        TreeSet<String> nonTerminalSet = new TreeSet<>();
        for (String someNonTerminal : nonTerminals) {
            if(someNonTerminal.equals("|")) {
                System.err.println("Terminal character can't be \"|\"!");
                throw new Exception();
            }

            if(someNonTerminal.length() != 1) {
                System.err.println("NonTerminal must contains only one symbol");
                throw new Exception();
            }

            if(Character.isDigit(someNonTerminal.charAt(0))) {
                System.err.println("NonTerminal can't be a numeric symbol");
                throw new Exception();
            }

            nonTerminalSet.add(someNonTerminal.toUpperCase());
        }
        //System.out.println(nonTerminalSet.size());
        String[] tempArr = new String[nonTerminalSet.size()];
        for (int i = 0; i < tempArr.length; i++) {
            tempArr[i] = nonTerminalSet.pollFirst();
        }
        return tempArr;
    }

    /**
     * Переводим в верхний регистр и проверяем на наличие в списке нетерминальных символов
     */
    public static String validateStartRule(String startRule, String[] nonTerminals) throws Exception {
        startRule = startRule.toUpperCase();
        if(!Arrays.asList(nonTerminals).contains(startRule)) {
            System.err.println("Unknown nonTerminal symbol used in start rule");
            throw new Exception();
        }

        return startRule;
    }

    public static String[] validateRules(String[] rules, char[] terminals, String[] nonTerminals) throws Exception {
        for(String rule : rules) {
            if(!Character.isUpperCase(rule.charAt(0))) {
                System.err.println("NonTerminal must be in upper case");
                throw new Exception();
            }
            for (int i = 3; i < rule.length(); i++) {
                if(rule.charAt(i) == '|' || rule.charAt(i) == '!') {
                    continue;
                }
                if (Character.isDigit(rule.charAt(i)) && !isElementInArray(rule.charAt(i), terminals)) {
                    System.err.println("Unknown numeric terminal in rule!");
                    throw new Exception();
                }
                if(Character.isLowerCase(rule.charAt(i)) && !isElementInArray(rule.charAt(i), terminals)) {
                    System.err.println("Wrong terminal character in rule!");
                    throw new Exception();
                }
                if(Character.isUpperCase(rule.charAt(i)) && !isElementInArray(rule.charAt(i), nonTerminals)) {
                    System.err.println("Wrong nonTerminal character in rule!");
                    throw new Exception();
                }
            }
        }

        return rules;
    }

    public static boolean isElementInArray(char element, String[] arr) {
        for (String s : arr) {
            if (element == s.charAt(0)) return true;
        }
        return false;
    }

    public static boolean isElementInArray(char element, char[] arr) {
        for (char c : arr) {
            if (c == element) return true;
        }
        return false;
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
