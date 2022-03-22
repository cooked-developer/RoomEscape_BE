package com.project.roomescape.controller;

import com.project.roomescape.requestDto.RoomAddRequestDto;
import com.project.roomescape.requestDto.RoomRequestDto;
import com.project.roomescape.responseDto.RoomResponseDto;
import com.project.roomescape.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
public class RoomController {

    private final RoomService roomService;

    // 방 개설하기 // request를 받아
    @PostMapping("/rooms")
    public RoomResponseDto createRoom(@RequestBody RoomRequestDto roomRequestDto) {
        return roomService.createRoom(roomRequestDto);
    }


    // 방 조회하기
    @GetMapping("/rooms/{roomId}")
    public RoomResponseDto getRoom(@PathVariable Long roomId) {
        return roomService.getRoom(roomId);
    }


    // 방 참여하기
    @PostMapping("/rooms/{roomId}")
    public void addMember(@PathVariable Long roomId, @RequestBody RoomAddRequestDto roomAddRequestDto) {
        roomService.addMember(roomId, roomAddRequestDto);
    }


    // 방 리스트 조회하기
    @GetMapping("/rooms/pages/{page}")
    public List<RoomResponseDto> getAllRooms(@PathVariable int page) {
        return roomService.getAllRooms(page);
    }


    // 방 삭제하기
//    @DeleteMapping("/room/{roomId}")
//    public void deleteRoom(@PathVariable Long roomId) {
//        roomService.deleteRoom(roomId);
//    }




}
