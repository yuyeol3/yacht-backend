package io.github.yuyeol3.yachtbackend.gameroom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

public record ParticipatedState(
        long userId,
        String userNick,
        boolean isReady
) {
}
