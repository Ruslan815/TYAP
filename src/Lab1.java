import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Lab1 {

    /**
     * G = {01; SZ, A; S -> 00S | 11S | 01A | 10A | !, A -> 00A | 11A | 01S | 10S; S}
     */
    private static final int LIMIT_OF_STEPS = 100;
    private static String grammar = "G = {01; S, A; S -> 00S | 11S | 01A | 10A | !, A -> 00A | 11A | 01S | 10S; S}";
    //private static String grammar = "G = {01; S, A; S -> 1A | 0A, A -> 1 | 0 | !; S}";
    private static boolean outputType; // false - L, true - R
    private static int startLength;
    private static int endLength;
    private static Map<String, String[]> mapOfRules = new HashMap<>();
    private static int stepCounter = 0;
    private static Set<Character> nonTerminalsSet;
    private static List<String> listOfRuleChain = new LinkedList<>();
//    private static final char[] notAllowedCharacters = {'!'};

    public static void inputData() throws IOException {
        Scanner scanner = new Scanner(System.in);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Если вы хотите использовать грамматику ПО УМОЛЧАНИЮ введите цифру 0, \n" +
                "если вы хотите ввести СВОЮ грамматику введите цифру 1: ");
        int isUserGrammar = scanner.nextInt();
        if (isUserGrammar == 1) {
            System.out.println("Введите грамматику (G = {\"\"; [ , ]; [ , ]; \"\"}): ");
            grammar = reader.readLine();
//            grammar = scanner.next();
            //System.out.println(inputStr);
        } else if (isUserGrammar != 0) {
            System.err.println("Wrong type of input grammar mode!");
            throw new IOException();
        }

        System.out.print("Введите тип вывода грамматики (левосторонний - L, правосторонний - R): ");
        String outputTypeStr = scanner.next();
//        System.out.println(outputTypeStr);
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

    public static Grammar parseGrammar() throws Exception {
        grammar = grammar.replace(" ","");
        grammar = grammar.replace("{","");
        grammar = grammar.replace("}","");
        grammar = grammar.substring(2);

        String[] grammarMembers = grammar.split(";");

        char[] terminals = grammarMembers[0].toCharArray();
        String[] nonTerminals = grammarMembers[1].split(",");
        String[] rules = grammarMembers[2].split(",");
        String startRule = grammarMembers[3];

        terminals = validateTerminals(terminals);
        nonTerminals = validateNonTerminals(nonTerminals);
        startRule = validateStartRule(startRule, nonTerminals);
        validateRules(rules, terminals, nonTerminals);

//        System.out.println(Arrays.toString(nonTerminals));

        System.out.println(Arrays.toString(grammarMembers));

        return new Grammar(terminals, nonTerminals, rules, startRule);
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

    public static void validateRules(String[] rules, char[] terminals, String[] nonTerminals) throws Exception {
        nonTerminalsSet = new HashSet<>();
        for(String rule : rules) {
            if(!Character.isUpperCase(rule.charAt(0))) {
                System.err.println("NonTerminal must be in upper case");
                throw new Exception();
            } else if(!isElementInArray(rule.charAt(0), nonTerminals)) {
                System.err.println("Character is not nonTerminal");
                throw new Exception();
            } else {
                nonTerminalsSet.add(rule.charAt(0));
            }

            if(rule.length() <= 3) {
                System.err.println("Rule length must be more than 3 characters");
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
                if(!Character.isUpperCase(rule.charAt(i)) && !isElementInArray(rule.charAt(i), terminals)) { //!!!!!!!!!!!!!
                    System.err.println("Wrong terminal character in rule!");
                    throw new Exception();
                }
                if(Character.isUpperCase(rule.charAt(i)) && !isElementInArray(rule.charAt(i), nonTerminals)) {
                    System.err.println("Wrong nonTerminal character in rule!");
                    throw new Exception();
                }
            }

            if (rule.charAt(rule.length() - 1) != '!' && (!isElementInArray(rule.charAt(rule.length() - 1), terminals) && !isElementInArray(rule.charAt(rule.length() - 1), nonTerminals))) {
                System.err.println("Rule must finish by terminal or nonTerminal!");
                throw new Exception();
            }
        }

        if(nonTerminalsSet.size() != nonTerminals.length) {
            System.err.println("All nonTerminal must be used in rules!");
            throw new Exception();
        }
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

    public static void prepareForGeneration(String[] nonTerminals, String[] rules) {
        //Заполняем мапу правил
        for (String someNonTerminal : nonTerminals) {
            String[] arrOfRules = null;
            for (String someRule : rules) {
                if (someRule.charAt(0) == someNonTerminal.charAt(0)) {
                    arrOfRules = someRule.substring(3).split("\\|");
                    break;
                }
            }
            mapOfRules.put(someNonTerminal, arrOfRules);
        }

        /*for (char someNonTerminal : nonTerminals) {
            String[] someRulesArr = mapOfRules.get(someNonTerminal);
            ArrayList<Integer> arr = new ArrayList<>();
            for (String someRule : someRulesArr) {
                int countOfTerminalsToExit = 0;
                for (int i = 0; i < someRule.length(); i++) {
                    if (Character.isLowerCase(someRule.charAt(i)) || Character.isDigit(someRule.charAt(i))) {
                        countOfTerminalsToExit++;
                    } else if (Character.isUpperCase(someRule.charAt(i))) {
                        countOfTerminalsToExit = 0;
                        break;
                    } else if (someRule.charAt(i) == '!') {
                        countOfTerminalsToExit = -1;
                        break;
                    }
                }

                if (countOfTerminalsToExit == -1) {
                    arr.add(0);
                } else if (countOfTerminalsToExit > 0) {
                    arr.add(countOfTerminalsToExit);
                }
            }
            if (!arr.isEmpty()) {
                exitMap.put(someNonTerminal, arr);
            }
        }*/
    }

    public static void generateLanguageChains(String currentNonTerminal, String currentChain, int currentLengthInTerminals) {
//        System.out.println(stepCounter);
//        System.out.println(currentChain);
        listOfRuleChain.add(currentChain);
        stepCounter++;

        // Если сгенерировали цепочку длины, больше чем надо, то дальше не генерируем
        if (currentLengthInTerminals > endLength) {
            stepCounter--;
            listOfRuleChain.remove(listOfRuleChain.size() - 1);
            return;
        }

        // Если нашли цепочку подходящей длины
        if (currentLengthInTerminals >= startLength) {
            boolean isNonTerminalExistInChain = false;
            for (int i = 0; i < currentChain.length(); i++) {
                if (Character.isUpperCase(currentChain.charAt(i))) { // If nonTerminal found in current chain
                    isNonTerminalExistInChain = true;
                }
            }
            if (!isNonTerminalExistInChain) {
                currentChain = currentChain.replaceAll("!", "");
                System.out.println(currentChain);
                System.out.println(listOfRuleChain);
                stepCounter--;
                listOfRuleChain.remove(listOfRuleChain.size() - 1);
                return;
            }
        }

        // S->00SAB|11S|01A|10A|!
        // A->00A|11A|01S|10S|1|0S0
        // S, "S", 0
        // S, 00S, 2
        // S, 0000S, 4
        String previousChain = currentChain;
        String nonTerminalForSteps = currentNonTerminal;
//        System.out.println(nonTerminalForSteps);
        for (String currentRule : mapOfRules.get(nonTerminalForSteps)) { // правосторонняя - reverse -> (берём левый нетерминал) replace -> reverse
//            currentChain = previousChain.replace(String.valueOf(nonTerminalForSteps), currentRule);
//            currentChain = replaceCharacterInString(nonTerminalForSteps, currentRule, previousChain);
            if(outputType) { // если правосторонняя
                previousChain = reverseString(previousChain);
                currentChain = reverseString(previousChain.replaceFirst(String.valueOf(nonTerminalForSteps), reverseString(currentRule)));
                previousChain = reverseString(previousChain);
            } else {
                currentChain = previousChain.replaceFirst(String.valueOf(nonTerminalForSteps), currentRule);
            }

            currentLengthInTerminals = countOfTerminals(currentChain);

            boolean isNonTerminalFound = false;
            if(outputType) { // если правосторонняя
                currentChain = reverseString(currentChain);
            }
            for (int i = 0; i < currentChain.length(); i++) {
                if (Character.isUpperCase(currentChain.charAt(i))) {
                    isNonTerminalFound = true;
                    currentNonTerminal = String.valueOf(currentChain.charAt(i)); // левосторонняя если не делать ревёрс
                    break;
                }
            }
            if(outputType) { // если правосторонняя
                currentChain = reverseString(currentChain);
            }

            // Если найдена законченная цепочка неподходящей длины
            if (!isNonTerminalFound && (currentLengthInTerminals < startLength || currentLengthInTerminals > endLength)) {
                stepCounter--;
                listOfRuleChain.remove(listOfRuleChain.size() - 1);
                return;
            }
            if (stepCounter > LIMIT_OF_STEPS) { // Защита от зацикливания
                stepCounter--;
                listOfRuleChain.remove(listOfRuleChain.size() - 1);
                return;
            }

            generateLanguageChains(currentNonTerminal, currentChain, currentLengthInTerminals);
        }
        stepCounter--;
        listOfRuleChain.remove(listOfRuleChain.size() - 1);
    }

    public static String reverseString(String str) {
        return new StringBuilder(str).reverse().toString();
    }

    public static int countOfTerminals(String someChain) {
        int count = 0;
        for (int i = 0; i < someChain.length(); i++) {
            if (someChain.charAt(i) != '!' && (!Character.isUpperCase(someChain.charAt(i)) || Character.isDigit(someChain.charAt(i)))) {
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        try {
            inputData();
        } catch (IOException e) {
            System.err.println("Input data error!");
            return;
            //e.printStackTrace();
        }

        Grammar parsedGrammar;
        try {
            parsedGrammar = parseGrammar();
            System.out.println(parsedGrammar);
        } catch (Exception e) {
            System.err.println("Grammar parsing Exception!");
            return;
            //e.printStackTrace();
        }
        System.out.println("Grammar after parsing:\n" + parsedGrammar);

        prepareForGeneration(parsedGrammar.getNonTerminals(), parsedGrammar.getRules());
        /*for(Map.Entry<String, String[]> entry : mapOfRules.entrySet()) {
            System.out.println(entry.getKey() + " : " + Arrays.toString(entry.getValue()));
        }*/
        /*for(Map.Entry<Character, ArrayList<Integer> > entry : exitMap.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }*/

        generateLanguageChains(parsedGrammar.getStartRule(), String.valueOf(parsedGrammar.getStartRule()), 0);
    }
}
