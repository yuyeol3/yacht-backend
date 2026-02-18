package io.github.yuyeol3.yachtbackend.gameroom;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ParticipatedRepository {
    private final ConcurrentHashMap<Long, ConcurrentHashMap<Long, ParticipatedState>> roomParticipants;
    private final ConcurrentHashMap<Long, Long> userCurrentRoom;

    public ParticipatedRepository() {
        this.roomParticipants = new ConcurrentHashMap<>();
        this.userCurrentRoom = new ConcurrentHashMap<>();

    }

    public void save(Long roomId, Long userId, String userNick) {
        if (userCurrentRoom.containsKey(userId)) {
            return;
        }

        roomParticipants.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>())
                .put(userId, new ParticipatedState(userId, userNick, true));

        userCurrentRoom.put(userId, roomId);
    }

    public void leave(Long roomId, Long userId) {
        Map<Long, ParticipatedState> participants = roomParticipants.get(roomId);
        if (participants != null) {
            participants.remove(userId);

            if (participants.isEmpty()) {
                roomParticipants.remove(roomId);
            }
        }

        userCurrentRoom.remove(userId);
    }

    // 불변 리스트 반환
    public List<ParticipatedState> findMembersByRoomId(Long roomId) {
        Map<Long, ParticipatedState> participants = roomParticipants.get(roomId);
        if (participants == null) {
            return List.of();
        }
        return List.copyOf(participants.values());
    }

    public int count(Long roomId) {
        Map<Long, ParticipatedState> participants = roomParticipants.get(roomId);
        return  participants == null ? 0 : participants.size();
    }

}
