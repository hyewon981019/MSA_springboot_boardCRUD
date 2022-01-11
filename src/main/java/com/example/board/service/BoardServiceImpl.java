package com.example.board.service;

import com.example.board.common.FileUtils;
import com.example.board.dto.BoardDto;
import com.example.board.dto.BoardFileDto;
import com.example.board.mapper.BoardMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;

@Slf4j
@Service
public class BoardServiceImpl implements BoardService{

    @Autowired
    BoardMapper boardMapper;

    @Autowired
    FileUtils fileUtils;

    @Override
    public List<BoardDto> selectBoardList() throws Exception {
        //목록 조회 쿼리를 실행해서 실행결과를 반환하는 기능 구현
        return boardMapper.selectBoardList();
    }

    @Override
    public void insertBoard(BoardDto board, MultipartHttpServletRequest request) throws Exception {

        log.debug("insertBoard()");

        //FileUtils의 parseFileInfo 메서드로 업로드 파일을 처리하고, 파일 정보를 반환받아 처리
        //받환받은 파일 정보가 있으면 db에 저장

        boardMapper.insertBoard(board); //insert를 해야 보드아이디 생기므로 일단 저장
        int boardIdx = board.getBoardIdx();

        List<BoardFileDto> fileInfoList = fileUtils.parseFileInfo(boardIdx, request); //아이디와 리퀘스트를 넣어주면 리스트 반환

        if(!CollectionUtils.isEmpty(fileInfoList))//업로드된 파일이 있으면
        {
            //db에 저장해야함
            boardMapper.insertBoardFileList(fileInfoList);
        }

    }


    @Override
    public BoardDto selectBoardDetail(int boardIdx) throws Exception {
        boardMapper.updateHitCount(boardIdx);
        //int i = 100 / 0;// 오류 유발 코드
        BoardDto boardDto = boardMapper.selectBoardDetail(boardIdx);

        //첨부파일 목록을 조회해서 게시판 정보에 추가
        List<BoardFileDto> fileInfoList = boardMapper.selectBoardFileList(boardIdx); //인덱스를 주고 상세정보 받음
        boardDto.setFileInfoList(fileInfoList);

        return boardDto;
    }

    @Override
    public void updateBoard(BoardDto board) throws Exception {
        boardMapper.updateBoard(board);
    }

    @Override
    public void deleteBoard(BoardDto board) throws Exception {
        boardMapper.deleteBoard(board);
    }

    @Override
    public BoardFileDto selectBoardFileInfo(int idx, int boardIdx) throws Exception {
        return boardMapper.selectBoardFileInfo(idx, boardIdx);
    }


}
