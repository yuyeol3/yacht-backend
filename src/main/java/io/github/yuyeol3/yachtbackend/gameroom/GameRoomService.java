package io.github.yuyeol3.yachtbackend.gameroom;


import io.github.yuyeol3.yachtbackend.GenericDataResponse;
import io.github.yuyeol3.yachtbackend.error.BusinessException;
import io.github.yuyeol3.yachtbackend.error.ErrorCode;
import io.github.yuyeol3.yachtbackend.user.User;
import io.github.yuyeol3.yachtbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameRoomService {

    private final GameRoomRepository gameRoomRepository;
    private final ParticipatedRepository participatedRepository;
    private final UserRepository userRepository;

    public GenericDataResponse<Long> createRoom(GameRoomCreateRequest gameRoomCreateRequest,
                                                UserDetails userDetails
                                                ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        User user = userRepository.findById(userId).orElseThrow();

        GameRoom gameRoom = new GameRoom(user, gameRoomCreateRequest.roomName());

        return new GenericDataResponse<>(gameRoomRepository.save(gameRoom).getId());
    }

    public Slice<GameRoomResponse> getRooms(Pageable pageable) {
        return gameRoomRepository.findGameRooms(pageable);
    }

    public GameRoomResponse getRoomById(Long roomId) {
        GameRoom gameRoom = gameRoomRepository.findById(roomId)
                .orElseThrow(()->new BusinessException(ErrorCode.NOT_FOUND));

        return GameRoomResponse.from(gameRoom);
    }


    public GenericDataResponse<String> addParticipant(Long roomId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                ()->new BusinessException(ErrorCode.UNAUTHORIZED)
        );

        GameRoom gameRoom = gameRoomRepository.findById(roomId).orElseThrow(
                ()->new BusinessException(ErrorCode.NOT_FOUND)
        );

        participatedRepository.save(roomId, userId, user.getNickname());
        return new GenericDataResponse<>(user.getNickname());
    }

    public GenericDataResponse<String> removeParticipant(Long roomId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                ()->new BusinessException(ErrorCode.UNAUTHORIZED)
        );

        GameRoom gameRoom = gameRoomRepository.findById(roomId).orElseThrow(
                ()->new BusinessException(ErrorCode.NOT_FOUND)
        );

        participatedRepository.leave(roomId, userId);
        return new GenericDataResponse<>(user.getNickname());
    }

}
