package io.github.yuyeol3.yachtbackend.game;

import io.github.yuyeol3.yachtbackend.gameroom.GameRoom;
import io.github.yuyeol3.yachtbackend.gameroom.GameRoomRepository;
import io.github.yuyeol3.yachtbackend.gameroom.ParticipatedRepository;
import io.github.yuyeol3.yachtbackend.gameroom.dto.ParticipatedState;
import io.github.yuyeol3.yachtbackend.user.User;
import io.github.yuyeol3.yachtbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameResultService {
    private final ParticipatedRepository participatedRepository;
    private final GameRepository gameRepository;
    private final GameRoomRepository gameRoomRepository;
    private final PlayedRepository playedRepository;
    private final UserRepository userRepository;


    @Transactional
    public void saveGameResults(GameState state) {
        // 게임 기본 정보 저장
        Game game = Game.builder()
                .startedAt(state.startedAt())
                .endedAt(LocalDateTime.now())
                .build();

        game = gameRepository.save(game);

        // [[userId, score],...]
        Long[][] userScores = new Long[state.turnList().size()][2];
        int idx = 0;
        for (Long userId : state.turnList()) {
            Optional<ParticipatedState> ps = participatedRepository.findByMemberIdAndRoomId(state.roomId(), userId);
            if (ps.isEmpty()) {
                userScores[idx][0] = userId;
                userScores[idx][1] = -1L;
            }
            else {
                userScores[idx][0] = userId;
                userScores[idx][1] = state.scores().get(userId).total().longValue();
            }
            idx++;
        }

        // 점수 기준으로 내림차순 정렬
        Arrays.sort(userScores, (a, b)->Long.compare(b[1], a[1]));

        // 등수 구하기
        int[] ranks = new int[state.turnList().size()];
        boolean isDraw = true;
        for (int i = 0; i < state.turnList().size(); i++) {
            if (i == 0) ranks[i] = 1;
            else ranks[i] = ranks[i - 1] + (userScores[i][1].equals(userScores[i - 1][1]) ? 0 : 1);

            isDraw = isDraw && (ranks[i] == 1); // 모든 사람이 1등이면 모두 무승부
        }
        // 방에 사람이 혼자 남아있지 않아야 함
        isDraw = isDraw && !(participatedRepository.findMembersByRoomId(state.roomId()).size() == 1);

        // 사용자별 played 결과 생성 및 저장
        for (int i = 0; i < idx; i++) {
            int rank = ranks[i];
            Long userId = userScores[i][0];
            Long score = userScores[i][1];

            User user = userRepository.findById(userId).orElse(null);
            if (user == null) continue;

            Played.PlayedBuilder pb = Played.builder()
                    .game(game)
                    .user(user)
                    .rank(rank);
            Played p;
            if (score == -1) {
                p = pb.score(0).gameResult(GameResult.LOSE).build();
            }
            else {
                if (isDraw) {
                    pb = pb.gameResult(GameResult.DRAW);
                }
                else {
                    pb = pb.gameResult(
                            rank <= (state.turnList().size() / 2) ? GameResult.WIN : GameResult.LOSE
                    );
                }

                p = pb.score(score.intValue()).build();
            }

            playedRepository.save(p);
        }
        gameRoomRepository.findById(state.roomId()).ifPresent(GameRoom::end);
    }

}
