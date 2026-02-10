package io.github.yuyeol3.yachtbackend.game;

import io.github.yuyeol3.yachtbackend.TimeEntity;
import io.github.yuyeol3.yachtbackend.user.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "participated")
public class Participated {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_room_id")
    private GameRoom gameRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
