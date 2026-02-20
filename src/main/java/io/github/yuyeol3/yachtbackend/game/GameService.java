package io.github.yuyeol3.yachtbackend.game;

import io.github.yuyeol3.yachtbackend.error.BusinessException;
import io.github.yuyeol3.yachtbackend.error.ErrorCode;
import io.github.yuyeol3.yachtbackend.game.dto.GameAction;
import io.github.yuyeol3.yachtbackend.game.dto.UserScoreBoard;
import io.github.yuyeol3.yachtbackend.gameroom.ParticipatedRepository;
import io.github.yuyeol3.yachtbackend.gameroom.ParticipatedState;
import io.github.yuyeol3.yachtbackend.user.User;
import io.github.yuyeol3.yachtbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameService {

    private final GameStateRepository gameStateRepository;
    private final ParticipatedRepository participatedRepository;
    private final GameResultService gameResultService;
    private final GameUtil gameUtil;


    public GameState processAction(Long roomId, Long userId, GameAction action) {
        GameState state = gameStateRepository.update(roomId, currentState-> {
            if (!currentState.curTurnUserId().equals(userId)) {
                throw new BusinessException(ErrorCode.NOT_YOUR_TURN);
            }

            return switch (action.type()) {
                case ROLL -> rollDice(currentState, action);
                case SELECT_SCORE -> selectScore(currentState, userId, action);
                case KEEP_TOGGLE -> keepToggleDice(currentState, action);
                default -> currentState;
            };
        });

        if (state.round() >= 13) gameResultService.saveGameResults(state);
        return state;
    }

    private GameState keepToggleDice(GameState state, GameAction action) {
        if (state.leftRollCnt() == 3)
            throw new BusinessException(ErrorCode.NOT_ROLLED);


        List<Boolean> keep = new ArrayList<>(state.kept());
        for (int keepIdx : action.keepIndices()) {
            if (0 <= keepIdx && keepIdx < 5) {
                keep.set(keepIdx, !keep.get(keepIdx));
            }
        }

        return state
                .toBuilder()
                .kept(List.copyOf(keep))
                .build();
    }

    private GameState rollDice(GameState state, GameAction action) {
        if (state.leftRollCnt() <= 0) {
            throw new BusinessException(ErrorCode.ROLL_CHANCE_OVER);
        }

        List<Integer> newDice = new ArrayList<>(state.dice());

        for (int i = 0; i < 5; i++) {
            if (!state.kept().get(i))
                newDice.set(i, (int) (Math.random() * 6) + 1);
        }

        return state.toBuilder()
                .dice(List.copyOf(newDice))
                .leftRollCnt(state.leftRollCnt() - 1)
                .build();
    }

    private GameState selectScore(GameState state, Long userId, GameAction action) {
        if (state.leftRollCnt() == 3)
            throw new BusinessException(ErrorCode.NOT_ROLLED);

        String category = action.scoreCategory();
        int score = gameUtil.calculateScore(state.dice(), category);

        Map<Long, UserScoreBoard> newScores = new HashMap<>(state.scores());
        UserScoreBoard myBoard = newScores.get(userId);

        if (myBoard.hasScore(category)) {
            throw new BusinessException(ErrorCode.FILLED_SCORE);
        }
        UserScoreBoard newBoard = UserScoreBoard.update(myBoard, category, score);
        // 보너스 판정 로직
        if (newBoard.upperScore() >= 63 && newBoard.bonus() == null) {
            newBoard = newBoard.toBuilder()
                    .bonus(35)
                    .total(newBoard.total() + 35)
                    .build();
        }

        newScores.put(userId, newBoard);
        int nextTurn = (state.turn() + 1) % state.turnList().size();
        int nextRound = nextTurn == 0 ? state.round() + 1 : state.round();

        // 다음 차례 확인
        while (nextRound <= 12 &&
                participatedRepository
                        .findByMemberIdAndRoomId(state.roomId(), state.turnList().get(nextTurn))
                        .isEmpty()
        ) {
            nextTurn = (nextTurn + 1) % state.turnList().size();
            nextRound = nextTurn == 0 ? state.round() + 1 : state.round();
        }


        // 게임 종료 여부 확인
//        if (nextRound >= 13) {
//            saveGameResults(state);
//        }
        //

        return state.toBuilder()
                .scores(newScores)
                .curTurnUserId(state.turnList().get(nextTurn))
                .leftRollCnt(3)
                .dice(List.of(0,0,0,0,0))
                .kept(List.of(false, false, false, false, false))
                .turnTimeoutTime(LocalDateTime.now())
                .round(nextRound)
                .turn(nextTurn)
                .build();
    }


}
