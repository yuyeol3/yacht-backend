package io.github.yuyeol3.yachtbackend.gameroom;

import io.github.yuyeol3.yachtbackend.GenericDataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;


@Slf4j
@Controller
@RequiredArgsConstructor
public class GameRoomWebSocketController {

    private final SimpMessagingTemplate template;
    private final GameRoomService gameRoomService;

    @MessageMapping("/rooms/{roomId}/enter")
    public void enterRoom(@DestinationVariable Long roomId, Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        GenericDataResponse<String> userNick = gameRoomService.addParticipant(roomId, userId);


    }

}
