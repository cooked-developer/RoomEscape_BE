package com.project.roomescape.service;

import com.project.roomescape.exception.CustomException;
import com.project.roomescape.exception.ErrorCode;
import com.project.roomescape.model.GameResource;
import com.project.roomescape.model.Room;
import com.project.roomescape.repository.GameResourceRepository;
import com.project.roomescape.repository.RoomRepository;
import com.project.roomescape.requestDto.GameLoadingDto;
import com.project.roomescape.requestDto.GameResourceRequestDto;
import com.project.roomescape.responseDto.GameLoadingResponseDto;
import com.project.roomescape.responseDto.GameResourceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.project.roomescape.exception.ErrorCode.ROOM_MEMBER_FULL;
import static com.project.roomescape.exception.ErrorCode.ROOM_NOT_FOUND;


@RequiredArgsConstructor
@Service
public class GameService {

    private final GameResourceRepository gameResourceRepository;
    private final RoomRepository roomRepository;




    // 게임 resource 저장하기
    public void saveGameResource(GameResourceRequestDto gameResourceRequestDto) {
        // requestDto로 받은 type을 찾아준다
        String type = gameResourceRequestDto.getType();
        // requestDto로 받은 url을 찾아준다
        String url = gameResourceRequestDto.getUrl();
        // gameResource 인스턴스에 담아준다
        GameResource gameResource = new GameResource(type, url);
        // repository에 저장해준다
        gameResourceRepository.save(gameResource);
    }


    // 게임 시작하기  (type이 gameRunFile인 url만을 찾아서 보내주기)
    public GameResourceResponseDto getGameResource() {
        // 모든 gameResource를 찾은 다음
        List<GameResource> gameResourceList = gameResourceRepository.findAll();
        // 반환할 responseDto를 null 값으로 선언
        GameResourceResponseDto gameResourceResponseDto = null;
        // for문을 돌려서
        for (GameResource eachGameResouce : gameResourceList) {
            // 각각의 url을 찾아서
            String url = eachGameResouce.getUrl();
            // 찾은 url중에서 type이 gameRunFile이면
            if (eachGameResouce.getType().equals("gameRunFile")) {
                // gameRunFile type의 url을 responseDto에 담아서
                gameResourceResponseDto = new GameResourceResponseDto(url);
            }
        }
        // 반환해준다.
        return gameResourceResponseDto;
    }


//    // Test용  userP1~5,gameRunFile의 url 등록 메소드 - TestDataRunner에서 사용
//    public void testRegisterProduct(GameResourceRequestDto gameResourceRequestDto) {
//        GameResource gameResource = new GameResource(gameResourceRequestDto);
//        gameResourceRepository.save(gameResource);
//    }

    public GameLoadingResponseDto checkGameLoading(GameLoadingDto gameLoadingDto) {
        Room room;
        GameLoadingResponseDto gameLoadingResponseDto= new GameLoadingResponseDto();
        Optional<Room> temp = roomRepository.findById(gameLoadingDto.getRoomId());
        if(temp.isPresent()) {
            room = temp.get();
        } else {
            throw new CustomException(ROOM_NOT_FOUND);
        }

//한명씩 로딩이 끝날때마다
        if(room.getUserList().size() > room.getLoadingCount() + 1) {
            gameLoadingResponseDto.setCheck(null);
            room.setLoadingCount(room.getLoadingCount() + 1);
        } else if(room.getUserList().size() == room.getLoadingCount() + 1){
            gameLoadingResponseDto.setCheck("true");
            room.setStartAt(System.currentTimeMillis());
            room.setLoadingCount(room.getLoadingCount() + 1);
        } else {
            throw new CustomException(ROOM_MEMBER_FULL);
        }

        for(int i = 0; i < room.getUserList().size(); i++) {
            if (room.getUserList().get(i).getUserId().equals(gameLoadingDto.getUserId())) {
                room.getUserList().get(i).setLoading(true);
            }
        }
        roomRepository.save(room);

        gameLoadingResponseDto.setUserId(null);

        return gameLoadingResponseDto;
    }

//    로딩중에 누군가가 한명 나갔는데 나머지 인원들은 전부 충족된 경우.
    public  Boolean exitDuringLoading(Room room) {
        if(room.getLoadingCount() > 0 && room.getUserList().size() == room.getLoadingCount()) {
            return true;
        } else{
            return false;
        }
    }

    public void startGame(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(()-> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        room.setStartAt(System.currentTimeMillis());
        roomRepository.save(room);

    }
}