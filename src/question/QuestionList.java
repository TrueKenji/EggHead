/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package question;

import bot.PlayerBot;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import model.Stack;
import utils.ArrayIndexDescendingComparator;
import logger.Logger;
import utils.ArrayComprehension;

/**
 * Question list: 1.Wie hoch ist die größte Anzahl desselben Buchstabens auf
 * allen Kartenhaltern zusammen? 2.Welcher Buchstabe überwiegt? 3.Wieviele
 * Buchstaben kommen insgesamt nur ein einziges Mal vor? 4.Sehen Sie öfter die
 * Kombination A und E oder die Kombination B und G? 5.Sehen Sie öfter die
 * Kombination B und E oder die Kombination C und D? 6.Fehlen irgendwelche
 * Buchstaben? 7.Wie oft sehen sie die Karten D, E und G insgesamt? 8.Kommt ein
 * Buchstabe öfter vor als alle anderen? Wenn ja, wer hat davon die meisten?
 * 9.Kommt ein Buchstabe öfter vor als alle anderen? Wenn ja, auf wie vielen
 * Kartenhaltern erscheint dieser Buchstabe? 10.Wie viele verschieden Buchstaben
 * sehen Sie? 11.Wieviele Buchstaben fehlen? 12.Welcher Buchstabe ist der
 * seltenste? 13.Wieviele Buchstaben gibt es insgesamt nur zwei Mal? 14.Auf
 * welchem Kartenhalter sehen Sie die größte Kartenzahl B, C und D in beliebiger
 * Häufung und Kombination? 15.Wieviele Buchstaben erscheinen dreimal und öfter?
 * 16.Sehen Sie einen Buchstaben mindestens viermal? 17.Hat Ihr linker Nachbar
 * einen Buchstaben, der sonst nirgendwo vorkommt? Wenn ja, wie viele? 18.Sehen
 * Sie auf einem Kartenhalter zwei oder mehr gleiche Buchstaben? Wie oft?
 * 19.Sehen Sie die gleiche Kombination von zwei Buchstaben auf zwei oder mehr
 * Kartenhaltern? 20.Sehen Sie mindestens einen Buchstaben auf jedem
 * Kartenhalter? 21.Beantworten Sie diesmal keine Frage, sondern mischen Sie
 * alle Fragen gründlich 22.Sie dürfen jetzt ungestraft raten, was Sie auf dem
 * Kartenhalter haben Wenn Sie falsch geraten haben, verlieren Sie nicht Ihre
 * Karten, Beantworten Sie dann die nächste Frage auf der nächsten Karte (3x)
 * 23.Sie dürfen eine bieliebige Karte vom Kartenhalter nehmen, Geben Sie diese
 * ohne den Buchstaben anzuschauen ins Spielkartenpaket zurück, Sie brauchen
 * jetzt also eine Karte weniger zu erraten. Beantworten Sie dann die Frage auf
 * der nächsten Karte
 */
public class QuestionList {

    private static final Logger logger = Logger.getLogger();
    private static final List<Integer> allIDs = generateAllIDs();
    private static final List<Integer> allNumbers = ArrayComprehension.generateStream(0, Stack.CARDSSIZE - 1);

    private static List<Integer> generateAllIDs() {
        return ArrayComprehension.generateStream(0, 4);
    }

    /**
     * return minimal frequent number
     *
     * @param cards
     * @return int, the minimal frequent number or -1 if ambigious
     */
    public static int minimalFreqNumberValue(Map<PlayerBot, Stack> cards) {
        return QuestionUtilities.getArgMin(cards);
    }

    /**
     * return identity of player who has highest amount of the unique number,
     * which appears most often in total
     *
     * @param cards
     * @return int, id of the player, or -1 if ambigious (either multiple
     * players, or no unique number)
     */
    public static int playerWithNumberOfHighestFreq(Map<PlayerBot, Stack> cards) {
        int[] playerCounts = new int[cards.size()];
        for (Map.Entry<PlayerBot, Stack> entry : cards.entrySet()) {
            playerCounts[entry.getKey().getID()] = entry.getValue().countOccurences(QuestionUtilities.getArgMax(cards));
        }
        return QuestionUtilities.getFirstSortedValue(new ArrayIndexDescendingComparator(playerCounts)).x;
    }

    /**
     * return amount of players, who have the number which appears most often in
     * total
     *
     * @param cards
     * @return int, id of the player, or -1 if ambigious (no unique number)
     */
    public static int numPlayersHavingHighestFreqNumber(Map<PlayerBot, Stack> cards) {
        int mostFrequentNumber = QuestionUtilities.getArgMax(cards);
        if (mostFrequentNumber == -1) {
            return -1;
        }
        int sum = 0;
        for (Map.Entry<PlayerBot, Stack> entry : cards.entrySet()) {
            sum += entry.getValue().contains(mostFrequentNumber) ? 1 : 0;
        }
        return sum;
    }

    /**
     * return whether left neighbour (id-1) has a (playerwise) unique card Note:
     * If played with an additional bot, the left neighbour is id = 0 Question
     * is still "symmetric"
     *
     * @param cards
     * @return int, 1 if true or 0 else
     */
    public static int leftNeighbourHasUniqueNumberBool(Map<PlayerBot, Stack> cards) {
        List<Integer> presentIDs = QuestionUtilities.getIDs(cards.keySet());
        List<Integer> all = new ArrayList(allIDs);
        all.removeAll(presentIDs);
        int id = (all.iterator().next() - 1) % (cards.size() + 1);
        PlayerBot player = QuestionUtilities.getPlayerByID(id, cards);
        int[] frequencies = QuestionUtilities.frequenciesExcept(cards, player);
        for (int card : cards.get(player)) {
            if (frequencies[card] == 0) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * return amount of missing numbers
     *
     * @param cards
     * @return int, amount of missing numbers
     */
    public static int amountOfMissingNumbers(Map<PlayerBot, Stack> cards) {
        return Stack.CARDSSIZE - QuestionUtilities.countNonZero(QuestionUtilities.frequencies(cards));
    }

    /**
     * return whether any number is missing
     *
     * @param cards
     * @return int, is at least one number missing
     */
    public static int missingNumberBool(Map<PlayerBot, Stack> cards) {
        boolean missingFlag = false;
        for (int i : QuestionUtilities.frequencies(cards)) {
            missingFlag = i == 0 ? true : missingFlag;
        }
        return missingFlag ? 1 : 0;
    }

    /**
     * return whether 0 and 2 appear more often than 1 and 3?
     *
     * @param cards
     * @return int, #(1,4) > #(2,3) or -1, if equally often
     */
    public static int zeroTwoMoreFreqThanOneThreeBool(Map<PlayerBot, Stack> cards) {
        return QuestionUtilities.countCombinations(cards, 0, 2, 1, 3);
    }

    /**
     * return whether at least 1 number is shared by everyone
     *
     * @param cards
     * @return int, 1 if yes, 0 else
     */
    public static int oneNumberEverywhereBool(Map<PlayerBot, Stack> cards) {
        ArrayList<Integer> candidates = new ArrayList(allNumbers);
        //candidates.addAll(ArrayComprehension.generateStream(0, Stack.CARDSSIZE));
        HashSet<Integer> removed = new HashSet();
        for (Map.Entry<PlayerBot, Stack> entry : cards.entrySet()) {
            for (Integer i : candidates) {
                if (!entry.getValue().contains(i)) {
                    removed.add(i);
                }
            }
        }
        return removed.size() < candidates.size() ? 1 : 0;
    }

    /**
     * return amount of numbers which appear twice in total
     *
     * @param cards
     * @return int, amount of numbers
     */
    public static int amountOfTwoOccurencyNumbers(Map<PlayerBot, Stack> cards) {
        return QuestionUtilities.frequencyOfValue(QuestionUtilities.frequencies(cards), 2);
    }

    /**
     * return amount of different numbers
     *
     * @param cards
     * @return int, amount of different numbers
     */
    public static int amountOfDistinctNumbers(Map<PlayerBot, Stack> cards) {
        return QuestionUtilities.countNonZero(QuestionUtilities.frequencies(cards));
    }

    /**
     * return number of occurrences of highest frequent number
     *
     * @param cards
     * @return int. the number of occurreneces of highest frequent number or -1,
     * if ambigious
     */
    public static int hightestFreqNumberFreq(Map<PlayerBot, Stack> cards) {
        return QuestionUtilities.getMax(cards);
    }

    /**
     * return amount of numbers appearing at least 3 times
     *
     * @param cards
     * @return int, amount of such numbers
     */
    public static int amountOfAtLeastTripleOccurencyNumbers(Map<PlayerBot, Stack> cards) {
        return QuestionUtilities.frequencyOfAtLeast(QuestionUtilities.frequencies(cards), 3);
    }

    /**
     * return whether 0 and 1 appear more often than 2 and 3?
     *
     * @param cards
     * @return int, #(0,4) > #(1,5) or -1, if equally often
     */
    public static int zeroOneMoreFreqThanTwoThreeBool(Map<PlayerBot, Stack> cards) {
        return QuestionUtilities.countCombinations(cards, 0, 1, 2, 3);
    }

    /**
     * return amount of players, having a pair or triple number
     *
     * @param cards
     * @return int, amount of such players
     */
    public static int numPlayersHavingPairOTriple(Map<PlayerBot, Stack> cards) {
        int sum = 0;
        for (Map.Entry<PlayerBot, Stack> entry : cards.entrySet()) {
            sum += entry.getValue().distinct() ? 0 : 1;
        }
        return sum;
    }

    /**
     * return whether the amount of at least one number is at least 4
     *
     * @param cards
     * @return int, 1 if yes, 0 if no
     */
    public static int quadrupelNumberBool(Map<PlayerBot, Stack> cards) {
        return QuestionUtilities.frequencyOfAtLeast(QuestionUtilities.frequencies(cards), 4) > 0 ? 1 : 0;
    }

    /**
     * return id of player who has highest amount of 0, 1, 2
     *
     * @param cards
     * @return int, id of player or -1, if ambigious
     */
    public static int playerWithHighestFreqZeroToTwo(Map<PlayerBot, Stack> cards) {
        int[] playerCounts = new int[cards.size()];
        for (Map.Entry<PlayerBot, Stack> entry : cards.entrySet()) {
            playerCounts[entry.getKey().getID()] = entry.getValue().countOccurences(0, 1, 2);
        }
        return QuestionUtilities.getArgMax(playerCounts);
    }

    /**
     * return highest frequent number
     *
     * @param cards
     * @return int, the highest frequent number or -1 if ambigious
     */
    public static int hightestFreqNumberValue(Map<PlayerBot, Stack> cards) {
        return QuestionUtilities.getArgMax(cards);
    }

    /**
     * return amount of 0, 1, 2
     *
     * @param cards
     * @return int, amount of 0, 1, 2
     */
    public static int amountOFZeroToTwo(Map<PlayerBot, Stack> cards) {
        int[] counts = QuestionUtilities.frequencies(cards);
//        SystemView.println(counts, SystemView.DEBUG_QUESTION);
        return counts[0] + counts[1] + counts[2];
    }

    /**
     * return amount of numbers appearing only once
     *
     * @param cards
     * @return int, the amount of numbers appearing only once
     */
    public static int uniqueNumbersAmount(Map<PlayerBot, Stack> cards) {
        int res = 0;
        for (int i : QuestionUtilities.frequencies(cards)) {
            res += i == 1 ? 1 : 0;
        }
        return res;
    }

    /**
     * return whether at least two players share at least two numbers TODO: Only
     * works for Stacksize == 3
     *
     * @param cards
     * @return int, 1 if yes, 0 else
     */
    public static int twoPlayerShareTwoNumbersBool(Map<PlayerBot, Stack> cards) {
        int flag = 0;
        for (Map.Entry<PlayerBot, Stack> entry : cards.entrySet()) {
            for (Map.Entry<PlayerBot, Stack> _entry : cards.entrySet()) {
                if (!entry.getKey().equals(_entry.getKey())) {
                    int sum = 0;
                    int[] countRight = QuestionUtilities.frequencies(_entry.getValue());
                    int[] countLeft = QuestionUtilities.frequencies(entry.getValue());
                    for (Integer i : allNumbers) {
                        sum += Math.min(countLeft[i], countRight[i]);
                    }
                    if (sum > 1) {
                        flag = 1;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * return number of occurrences of lowest frequent number
     *
     * @param cards
     * @return int. the number of occurreneces of lowest frequent number or -1,
     * if ambigious
     */
    public static int minimalFreqNumberFreq(Map<PlayerBot, Stack> cards) {
        return QuestionUtilities.getMin(cards);
    }

    /**
     * return amount of numbers appearing exactly 3 times
     *
     * @param cards
     * @return int, amount of such numbers
     */
    public static int amountOfTripleOccurencyNumbers(Map<PlayerBot, Stack> cards) {
        return QuestionUtilities.frequencyOfValue(QuestionUtilities.frequencies(cards), 3);
    }

    /**
     * return amount of numbers which appear at least twice
     *
     * @param cards
     * @return int, amount of numbers
     */
    public static int amountOfAtLeastTwoOccurencyNumbers(Map<PlayerBot, Stack> cards) {
        return QuestionUtilities.frequencyOfAtLeast(QuestionUtilities.frequencies(cards), 2);
    }

    public static enum Question {
        minimalFreqNumberValue,
        hightestFreqNumberFreq,
        hightestFreqNumberValue,
        uniqueNumbersAmount,
        twoPlayerShareTwoNumbersBool,
        oneNumberEverywhereBool,
        missingNumberBool,
        numPlayersHavingPairOTriple,
        playerWithNumberOfHighestFreq,
        numPlayersHavingHighestFreqNumber,
        amountOfDistinctNumbers,
        amountOfMissingNumbers,
        amountOfTwoOccurencyNumbers,
        quadrupelNumberBool,
        amountOfTripleOccurencyNumbers,
        amountOFZeroToTwo,
        playerWithHighestFreqZeroToTwo,
        leftNeighbourHasUniqueNumberBool,
        zeroOneMoreFreqThanTwoThreeBool,
        zeroTwoMoreFreqThanOneThreeBool,
        minimalFreqNumberFreq,
        amountOfAtLeastTwoOccurencyNumbers,
        amountOfAtLeastTripleOccurencyNumbers,
    }

}
