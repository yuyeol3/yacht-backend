package io.github.yuyeol3.yachtbackend.game;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GameUtil {

    public int calculateScore(List<Integer> dice, String category) {

        int[] scores = new int[7];

        for (int i = 0; i < dice.size(); i++) {
            scores[dice.get(i)]++;
        }

        return switch (category) {
            case "ONES" -> scores[1];
            case "TWOS" -> scores[2] * 2;
            case "THREES" -> scores[3] * 3;
            case "FOURS" -> scores[4] * 4;
            case "FIVES" ->  scores[5] * 5;
            case "SIXES" ->  scores[6] * 6;
            case "CHOICE" -> dice.stream().mapToInt(i->i).sum();
            case "FOUR_OF_A_KIND" -> calcFourOfAKind(dice, scores);
            case "FULL_HOUSE" -> calcFullHouse(dice, scores);
            case "S_STRAIGHT" -> calcSStraight(dice, scores);
            case "L_STRAIGHT" -> calcLStraight(dice, scores);
            case "YACHT" -> calcYacht(dice, scores);
            default -> 0;
        };
    }

    private int calcFourOfAKind(List<Integer> dice, int[] scores) {
        for (int i = 1; i <= 6; i++) {
            if (scores[i] >= 4) {
                return dice.stream().mapToInt(n->n).sum();
            }
        }

        return 0;
    }

    private int calcFullHouse(List<Integer> dice, int[] scores) {
        for (int i = 1;  i <= 6; i++) {
            for (int j = i+1; j <= 6; j++) {
                if (scores[i] == 2 && scores[j] == 3 ||
                    scores[i] == 3 && scores[j] == 2
                ) return dice.stream().mapToInt(n->n).sum();
            }
        }
        return 0;
    }

    private int calcSStraight(List<Integer> dice, int[] scores) {
        for (int i = 1; i <= 3; i++) {
            boolean flag = true;
            for (int j = i; j < 4 + i; j++) {
                flag = flag && (scores[j] >= 1);
            }
            if (flag) {
                return 15;
            }
        }

        return 0;
    }

    private int calcLStraight(List<Integer> dice, int[] scores) {
        for (int i = 1; i <= 2; i++) {
            boolean flag = true;
            for (int j = i; j < 5 + i; j++) {
                flag = flag && (scores[j] >= 1);
            }
            if (flag) {
                return 30;
            }
        }

        return 0;
    }

    private int calcYacht(List<Integer> dice, int[] scores) {
        for (int i = 1; i <= 6; i++) {
            if (scores[i] == 5)
                return 50;
        }

        return 0;
    }

}
