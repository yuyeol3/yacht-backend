package io.github.yuyeol3.yachtbackend.game;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GameState {
    private Long roomId;

    private Long currentTurnUserId;
    private int leftRollCount;
    private int round;

    private List<Integer> dice;
    private List<Boolean> kept;

//    private Map<Long, UserScoreBoard> scores;

}
