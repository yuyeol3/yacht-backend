package io.github.yuyeol3.yachtbackend.gameroom;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Builder(toBuilder = true)
public record ParticipatedState(
        long userId,
        String userNick,
        boolean isReady
) {
}
