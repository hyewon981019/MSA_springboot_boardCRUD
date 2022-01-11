package com.example.board.controller;

import com.example.board.dto.BoardDto;
import com.example.board.dto.BoardFileDto;
import com.example.board.service.BoardService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
public class BoardController {

    // 로거 생성
    //Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BoardService boardService;

    @RequestMapping("/board/openBoardList.do")
    public ModelAndView openBoardList() throws Exception {
        ModelAndView mv = new ModelAndView("/board/boardList");

        List<BoardDto> data = boardService.selectBoardList();
        mv.addObject("resultList", data);

        return mv;
    }

    // 게시글 등록 페이지에 대한 요청을 처리하는 컨트롤러
    @RequestMapping("/board/openBoardWrite.do")
    public String openBoardWrite() throws Exception {
        return "/board/boardWrite"; // 등록 화면 페이지를 반환
    }

    // 입력한 게시물을 저장(등록) 요청을 처리하는 컨트롤러
    @RequestMapping("/board/insertBoard.do")
    public String insertBoard(BoardDto board, MultipartHttpServletRequest request) throws Exception {
        //인자 타입에 맞는 객체를 스프링이 알아서 꽂아준다
        boardService.insertBoard(board, request);
        return "redirect:/board/openBoardList.do";
    }

    @RequestMapping("/board/openBoardDetail.do")
    public ModelAndView openBoardDetail(@RequestParam int boardIdx) throws Exception {
        ModelAndView mv = new ModelAndView("/board/boardDetail");

        BoardDto data = boardService.selectBoardDetail(boardIdx);
        mv.addObject("resultDetail", data);

        return mv;
    }

    @RequestMapping("/board/updateBoard.do")
    public String updateBoard(BoardDto board) throws Exception {
        boardService.updateBoard(board);
        return "redirect:/board/openBoardList.do";
    }

    @RequestMapping("/board/deleteBoard.do")
    public String deleteBoard(BoardDto board) throws Exception {
        boardService.deleteBoard(board);
        return "redirect:/board/openBoardList.do";
    }

    // /board/downloadBoardFile.do?idx=???&boardIdx=???
    @RequestMapping("/board/downloadBoardFile.do")
    public void downloadBoardFile(@RequestParam int idx, @RequestParam int boardIdx, HttpServletResponse response) throws Exception {
        // DB에서 파일 정보를 조회
        BoardFileDto boardFile = boardService.selectBoardFileInfo(idx, boardIdx);

        // 파일을 읽어서 호출한 곳으로 (응답을) 전달
        if (!ObjectUtils.isEmpty(boardFile)) {
            String fileName = boardFile.getOriginalFileName();
            // 저장된 파일을 읽어오는 기능
            byte[] files = FileUtils.readFileToByteArray(new File(boardFile.getStoredFilePath()));

            // 읽어온 파일을 응답(response)을 통해서 호출한 곳으로 전달
            response.setContentType("application/octet-stream");
            response.setContentLength(files.length);
            response.setHeader("Content-Disposition", "attachment; fileName=\"" + URLEncoder.encode(fileName, "UTF-8") + "\";");
            response.setHeader("Content-Transfer-Encoding", "binary");

            response.getOutputStream().write(files);
            response.getOutputStream().flush();
            response.getOutputStream().close();
        }
    }





}
