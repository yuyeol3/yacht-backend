package io.github.yuyeol3.yachtbackend.gameroom;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {

    @Query("""
        SELECT NEW io.github.yuyeol3.yachtbackend.gameroom.GameRoomResponse(
             gr.id,
             gr.roomName,
             h.nickname,
             COUNT(p)
        )
        FROM GameRoom gr
        JOIN gr.host h
        LEFT JOIN Participated p ON p.gameRoom = gr
        GROUP BY gr.id, gr.roomName, h.nickname
    """)
    Slice<GameRoomResponse> findGameRooms(Pageable pageable);

//    @Query("""
//        SELECT NEW io.github.yuyeol3.yachtbackend.gameroom.GameRoomResponse(
//             gr.id,
//             gr.roomName,
//             h.nickname,
//             COUNT(p)
//        )
//        FROM GameRoom gr
//        JOIN gr.host h
//        LEFT JOIN Participated p ON p.gameRoom = gr
//        WHERE gr.id = :roomId
//        GROUP BY gr.id, gr.roomName, h.nickname
//    """)
//    Optional<GameRoomResponse> findGameRoomById(Long roomId);
}
