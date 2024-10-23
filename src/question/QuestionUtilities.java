package question;

import bot.PlayerBot;
import bot.StackScenario;
import db.EggHeadWriter;
import utils.ArrayIndexAscendingComparator;
import utils.ArrayIndexComparator;
import utils.ArrayIndexDescendingComparator;
import java.util.ArrayList;
import java.awt.Point;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import model.Stack;
import question.QuestionList.Question;
import java.lang.reflect.InvocationTargetException;
import logger.Logger;
import utils.Combinatorics;

public class QuestionUtilities {

    private static Logger logger = Logger.getLogger();
    private static List<Question> questionStack = new ArrayList();
    public static int NUM_QUESTIONS = 20; // 18 and 19 must have Stack.CARDSSIZE >= 4
    private static final Map<Question, String> questionText = createQuestionTexts();

    private static Map<Question, String> createQuestionTexts() {
        HashMap<Question, String> map = new HashMap();
        map.put(Question.minimalFreqNumberValue, "What is the rarest number? -1 if tie");
        map.put(Question.hightestFreqNumberFreq, "How frequent is the most frequent number? -1 if tie");
        map.put(Question.hightestFreqNumberValue, "What is the most frequent number -1 if tie");
        map.put(Question.uniqueNumbersAmount, "How many unique numbers are there?");
        map.put(Question.twoPlayerShareTwoNumbersBool, "Do at least two players share at least two numbers?");
        map.put(Question.oneNumberEverywhereBool, "Is there a common number everywhere?");
        map.put(Question.missingNumberBool, "Are there numbers missing?");
        map.put(Question.numPlayersHavingPairOTriple, "How many players have a number twofold or more?");
        map.put(Question.playerWithNumberOfHighestFreq, "Which player has the most frequent number most frequently? -1 if tie");
        map.put(Question.numPlayersHavingHighestFreqNumber, "How many players have the most frequent number? -1 if tie");
        map.put(Question.amountOfDistinctNumbers, "How many distinct numbers are there?");
        map.put(Question.amountOfTwoOccurencyNumbers, "How many numbers appear twice in total?");
        map.put(Question.quadrupelNumberBool, "Is there a number appearing at least four times?");
        map.put(Question.amountOfTripleOccurencyNumbers, "How many numbers appear at least three times in total?");
        map.put(Question.amountOFZeroToTwo, "How often do the numbers 0 to 2 appear in total?");
        map.put(Question.playerWithHighestFreqZeroToTwo, "Which player has the numbers 0 to 2 most often?");
        map.put(Question.leftNeighbourHasUniqueNumberBool, "Does your left neighour have a unique number?");
        map.put(Question.zeroOneMoreFreqThanTwoThreeBool, "Per player, is the combination 0 and 1 more frequent than the combination 2 and 3?");
        map.put(Question.zeroTwoMoreFreqThanOneThreeBool, "Per player, is the combination 0 and 2 more frequent than the combination 1 and 3?");
        map.put(Question.amountOfMissingNumbers, "How many numbers are missing?");
        return map;
    }

    public static int countCombinations(Map<PlayerBot, Stack> cards, int... combi) {
        int diffCtr = 0;
        for (Stack s : cards.values()) {
            diffCtr += s.contains(combi[0]) && s.contains(combi[1]) ? 1 : (s.contains(combi[2]) && s.contains(combi[3]) ? -1 : 0);
        }
        return diffCtr > 0 ? 1 : (diffCtr == 0 ? -1 : 0);
    }

    public static int countNonZero(int[] counts) {
        int sum = 0;
        for (int i : counts) {
            sum += i != 0 ? 1 : 0;
        }
        return sum;
    }

    /**
     * returns min value of counts per number over all stacks
     *
     * @param cards
     * @return int, the value or frequency or -1 if ambigious
     */
    public static int getMin(Map<PlayerBot, Stack> cards) {
        return getFirstSortedValue(new ArrayIndexAscendingComparator(frequencies(cards))).y;
    }

    public static int getMax(Map<PlayerBot, Stack> cards) {
        return getMax(frequencies(cards));
    }

    public static int getArgMin(Map<PlayerBot, Stack> cards) {
        return getFirstSortedValue(new ArrayIndexAscendingComparator(frequencies(cards))).x;
    }

    public static int getArgMax(Map<PlayerBot, Stack> cards) {
        return getArgMax(frequencies(cards));
    }

    public static int getArgMax(int[] counts) {
        return getFirstSortedValue(new ArrayIndexDescendingComparator(counts)).x;
    }

    public static int getMax(int[] counts) {
        return getFirstSortedValue(new ArrayIndexDescendingComparator(counts)).y;
    }

    /**
     * count the occurences of each number and return a respective array
     *
     * @param cardsList the list of player hands
     * @return int[] arr: arr[i] = #occurrences of number i
     */
    public static int[] frequencies(Map<PlayerBot, Stack> cardsList) {
        int[] counts = new int[Stack.CARDSSIZE];
        for (Stack cards : cardsList.values()) {
            counts = utils.ArrayComprehension.add(counts, frequencies(cards));
        }
        return counts;
    }

    public static int[] frequenciesExcept(Map<PlayerBot, Stack> cards, PlayerBot except) {
        int[] counts = new int[Stack.CARDSSIZE];
        for (Map.Entry<PlayerBot, Stack> entry : cards.entrySet()) {
            if (!entry.getKey().equals(except)) {
                counts = utils.ArrayComprehension.add(counts, frequencies(entry.getValue()));
            }
        }
        return counts;
    }

    public static int[] frequencies(Stack cards) {
        int[] counts = new int[Stack.CARDSSIZE];
        for (Integer i : cards) {
            counts[i]++;
        }
        return counts;
    }

    public static int frequencyOfValue(int[] counts, int value) {
        int sum = 0;
        for (int i : counts) {
            sum += i == value ? 1 : 0;
        }
        return sum;
    }

    public static int frequencyOfAtLeast(int[] counts, int value) {
        int sum = 0;
        for (int i : counts) {
            sum += i >= value ? 1 : 0;
        }
        return sum;
    }

    /**
     * retrieve [0] entry in sorted counts, both number and frequency
     *
     * @param comparator
     * @return Point p: p.x = number, p.y = #occurrences. or null, if ambigious
     */
    public static Point getFirstSortedValue(ArrayIndexComparator comparator) {
        Integer[] indexes = comparator.createIndexArray();
        Arrays.sort(indexes, comparator);
        int[] counts = comparator.getArray();
        Point p = new Point(indexes[0], counts[indexes[0]]);
        return counts[indexes[0]] != counts[indexes[1]] ? p : new Point(-1, -1);
    }

    public static PlayerBot argMax(Map<PlayerBot, Integer> map) {
        int max = -1;
        PlayerBot maxPlayer = null;
        for (PlayerBot player : map.keySet()) {
            if (map.get(player) > max) {
                max = map.get(player);
                maxPlayer = player;
            }
        }
        return maxPlayer;

    }

    public static List<Integer> getIDs(Set<PlayerBot> players) {
        ArrayList<Integer> list = new ArrayList();
        for (PlayerBot player : players) {
            list.add(player.getID());
        }
        return list;
    }

    public static PlayerBot getPlayerByID(int id, Map<PlayerBot, Stack> cards) {
        for (PlayerBot player : cards.keySet()) {
            if (player.getID() == id) {
                return player;
            }
        }
        return null;
    }

    public static Set<Stack> getUnionExcept(Map<PlayerBot, Stack> cards, PlayerBot except) {
        Set<Stack> set = new HashSet();
        for (Entry<PlayerBot, Stack> entry : cards.entrySet()) {
            if (!entry.getKey().equals(except)) {
                set.addAll(new HashSet(entry.getValue()));
            }
        }
        return set;
    }

    public static Question next() {
        if (questionStack.isEmpty()) {
            questionStack = new ArrayList(Arrays.asList(Question.values()));  //Utilities.generateStream(0, NUM_QUESTIONS - 1);
        }
        return questionStack.remove(Combinatorics.RND.nextInt(questionStack.size()));
    }

    public static String getQuestionText(Question question) {
        return questionText.get(question);
    }

    public static int invokeQuestion(Map<PlayerBot, Stack> cards, Question question) {
        try {
            Method method = QuestionList.class.getMethod(question.toString(), Map.class);
            return (int) method.invoke(null, cards);
        } catch (IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            System.out.println(e);
        }
        throw new RuntimeException("Error in Method invokeQuestion");
    }

    public static void generateTestData() {
        Map<StackScenario, Map<Question, Integer>> questionTestMap = new HashMap();
        int numTests = 1000;
        for (int ctr = 0; ctr < numTests; ctr++) {
            Map<Question, Integer> testMap = new HashMap();
            int numPlayers = 4;
            StackScenario scenario = StackScenario.sample(numPlayers);
            logger.println(scenario, Logger.QUESTION);
            for (Question question : Question.values()) {
                int answer = invokeQuestion(scenario, question);
                logger.println(question + " " + answer, Logger.QUESTION);
                testMap.put(question, answer);
            }
            questionTestMap.put(scenario, testMap);
        }
        ArrayList<String> formattedData = formatData(questionTestMap);
        EggHeadWriter.write("_QuestionTestData.txt", formattedData);
    }

    public static ArrayList<String> formatData(Map<StackScenario, Map<Question, Integer>> questionTestMap) {
        ArrayList<String> result = new ArrayList();
        for (Map.Entry<StackScenario, Map<Question, Integer>> entry : questionTestMap.entrySet()) {
            String line = entry.getKey().format() + ":";
            for (Map.Entry<Question, Integer> questionEntry : entry.getValue().entrySet()) {
                line += questionEntry.getKey() + "," + questionEntry.getValue() + ";";
            }
            result.add(line.substring(0, line.length() - 1));
        }
        return result;
    }

    public static Question randomQuestion() {
        return utils.ArrayComprehension.randomEnum(Question.class);
    }

    public static void test() {
        logger.println("Starting Questions Test run", Logger.CONTROLLER);
        HashMap<StackScenario, HashMap<Question, Integer>> questionTestMap = readQuestionTestMap();
        int failCounter = 0;
        for (Map.Entry<StackScenario, HashMap<Question, Integer>> scenarioEntry : questionTestMap.entrySet()) {
            for (Map.Entry<Question, Integer> questionEntry : scenarioEntry.getValue().entrySet()) {
                int expectedAnswer = questionEntry.getValue();
                int actualAnswer = QuestionUtilities.invokeQuestion(scenarioEntry.getKey(), questionEntry.getKey());
                if (expectedAnswer != actualAnswer) {
                    failCounter++;
                    logger.println("Test failed. Question: " + questionEntry.getKey() + ". exp/act Answer: " + expectedAnswer + " " + actualAnswer, Logger.QUESTION);
                }
            }
        }
        if (failCounter == 0) {
            logger.println("Test finished. All tests successful!", Logger.QUESTION);
        } else {
            logger.println("Test finished. Failed tests: " + failCounter, Logger.QUESTION);
        }
    }

    public static void testOneQuestion() {
        HashMap<PlayerBot, Stack> cards = new HashMap();
        cards.put(new PlayerBot(0), new Stack(0, 0, 1));
        cards.put(new PlayerBot(1), new Stack(0, 0, 0));
        cards.put(new PlayerBot(2), new Stack(0, 1, 2));
        System.out.println(invokeQuestion(cards, Question.leftNeighbourHasUniqueNumberBool));
    }

    /**
     * read the static in/out data for testing Format is
     * 0,0,0;1,1,1;2,2,2:MinimalNumberValue,1;HighestNumberFreq,1; Highest Level
     * delimiter is : It delimits stack scenario and question/answer list Before
     * the : the stacks of the players are shown, delimited with ; and , After
     * the Questions and answer are shown, delimited by ; and ,
     *
     * @return HashMap, the data processed in a usable way
     */
    public static HashMap readQuestionTestMap() {
        ArrayList<List<String>> questionData = db.EggHeadReader.load("TestQuestions.txt", ":", false);
        HashMap<StackScenario, HashMap<Question, Integer>> testMap = new HashMap();
        for (List<String> row : questionData) {
            String scenarioString = row.get(0);
            StackScenario scenario = new StackScenario(scenarioString);
            String[] resultList = row.get(1).split(";");
            HashMap<Question, Integer> resultMap = new HashMap();
            for (String questionAnswer : resultList) {
                String[] delimitedQA = questionAnswer.split(",");
                resultMap.put(Question.valueOf(delimitedQA[0]), Integer.valueOf(delimitedQA[1]));
            }
            testMap.put(scenario, resultMap);
        }
        return testMap;
    }

}
