package com.example.board.service;

import com.example.board.dto.BoardDto;
import com.example.board.dto.BoardFileDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;

public interface BoardService {
    // 게시판 목록을 조회해서 반환하는 메서드를 정의
    List<BoardDto> selectBoardList() throws Exception;
    void insertBoard(BoardDto board, MultipartHttpServletRequest request) throws Exception;

    BoardDto selectBoardDetail(int boardIdx) throws Exception;

    void updateBoard(BoardDto board) throws Exception;

    void deleteBoard(BoardDto board) throws Exception;

    BoardFileDto selectBoardFileInfo(int idx, int boardIdx) throws Exception;

}
