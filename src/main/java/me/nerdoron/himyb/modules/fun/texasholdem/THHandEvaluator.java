package me.nerdoron.himyb.modules.fun.texasholdem;

import java.util.*;
import java.util.stream.Collectors;

public class THHandEvaluator {

    public static THHandRank evaluateBestHand(List<THCard> holeCards, List<THCard> communityCards) {
        List<THCard> combinedCards = new ArrayList<>(holeCards);
        combinedCards.addAll(communityCards);

        // To find the best hand, evaluate all possible combinations of 5 cards from the 7 available.
        List<THHandRank> THHandRanks = new ArrayList<>();
        for (List<THCard> combination : getCombinations(combinedCards, 5)) {
            THHandRanks.add(evaluateHand(combination));
        }

        // Return the highest hand rank found
        return Collections.max(THHandRanks, Comparator.comparingInt(THHandRank::getValue));
    }

    private static List<List<THCard>> getCombinations(List<THCard> cards, int n) {
        List<List<THCard>> combinations = new ArrayList<>();
        getCombinations(cards, n, 0, new ArrayList<>(), combinations);
        return combinations;
    }

    private static void getCombinations(List<THCard> cards, int n, int start, List<THCard> current, List<List<THCard>> result) {
        if (current.size() == n) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i < cards.size(); i++) {
            current.add(cards.get(i));
            getCombinations(cards, n, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    public static THHandRank evaluateHand(List<THCard> hand) {
        if (isRoyalFlush(hand)) return THHandRank.ROYAL_FLUSH;
        if (isStraightFlush(hand)) return THHandRank.STRAIGHT_FLUSH;
        if (isFourOfAKind(hand)) return THHandRank.FOUR_OF_A_KIND;
        if (isFullHouse(hand)) return THHandRank.FULL_HOUSE;
        if (isFlush(hand)) return THHandRank.FLUSH;
        if (isStraight(hand)) return THHandRank.STRAIGHT;
        if (isThreeOfAKind(hand)) return THHandRank.THREE_OF_A_KIND;
        if (isTwoPair(hand)) return THHandRank.TWO_PAIR;
        if (isOnePair(hand)) return THHandRank.ONE_PAIR;
        return THHandRank.HIGH_CARD;
    }

    private static boolean isRoyalFlush(List<THCard> hand) {
        return isStraightFlush(hand) && hand.stream().anyMatch(card -> card.getRank() == 14);
    }

    private static boolean isStraightFlush(List<THCard> hand) {
        return isFlush(hand) && isStraight(hand);
    }

    private static boolean isFourOfAKind(List<THCard> hand) {
        return getRankCounts(hand).containsValue(4);
    }

    private static boolean isFullHouse(List<THCard> hand) {
        Map<Integer, Integer> rankCounts = getRankCounts(hand);
        return rankCounts.containsValue(3) && rankCounts.containsValue(2);
    }

    private static boolean isFlush(List<THCard> hand) {
        String firstSuit = hand.get(0).getSuit();
        return hand.stream().allMatch(card -> card.getSuit().equals(firstSuit));
    }

    private static boolean isStraight(List<THCard> hand) {
        List<Integer> sortedRanks = hand.stream().map(THCard::getRank).distinct().sorted().collect(Collectors.toList());

        for (int i = 0; i < sortedRanks.size() - 1; i++) {
            if (sortedRanks.get(i + 1) - sortedRanks.get(i) != 1) {
                return false;
            }
        }
        return true;
    }

    private static boolean isThreeOfAKind(List<THCard> hand) {
        return getRankCounts(hand).containsValue(3);
    }

    private static boolean isTwoPair(List<THCard> hand) {
        Map<Integer, Integer> rankCounts = getRankCounts(hand);
        return Collections.frequency(new ArrayList<>(rankCounts.values()), 2) == 2;
    }

    private static boolean isOnePair(List<THCard> hand) {
        return getRankCounts(hand).containsValue(2);
    }

    private static Map<Integer, Integer> getRankCounts(List<THCard> hand) {
        Map<Integer, Integer> rankCounts = new HashMap<>();
        for (THCard card : hand) {
            rankCounts.put(card.getRank(), rankCounts.getOrDefault(card.getRank(), 0) + 1);
        }
        return rankCounts;
    }

    public static String compareHands(List<THCard> playerHand, List<THCard> dealerHand, List<THCard> communityCards) {
        THHandRank playerBestHand = evaluateBestHand(playerHand, communityCards);
        THHandRank dealerBestHand = evaluateBestHand(dealerHand, communityCards);

        if (playerBestHand.getValue() > dealerBestHand.getValue()) {
            return "player";
        } else if (dealerBestHand.getValue() > playerBestHand.getValue()) {
            return "dealer";
        } else {
            return "tie";
        }
    }

}
