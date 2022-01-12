package com.example.board.controller;

import com.example.board.dto.BoardDto;
import com.example.board.dto.BoardFileDto;
import com.example.board.service.BoardService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping(value="/api")
public class RestApiBoardController {
    @Autowired
    BoardService boardService;

    @RequestMapping(value="/board", method= RequestMethod.GET)
    public List<BoardDto> openBoardList() throws Exception {
//        ModelAndView mv = new ModelAndView("/board/restBoardList");
//
//        List<BoardDto> data = boardService.selectBoardList();
//        mv.addObject("resultList", data);

        return boardService.selectBoardList();
    }


//    @RequestMapping(value="/board/write", method=RequestMethod.GET)
//    public String openBoardWrite() throws Exception {
//        return "/board/restBoardWrite";
//    }

    @RequestMapping(value="/board/write", method=RequestMethod.POST)
    public void insertBoard(@RequestBody BoardDto board) throws Exception {
        boardService.insertBoard(board, null);
    }

    @RequestMapping(value="/board/{boardIdx}", method=RequestMethod.GET)
    public BoardDto openBoardDetail(@PathVariable("boardIdx") int boardIdx) throws Exception {
        return boardService.selectBoardDetail(boardIdx);

    }

    @RequestMapping(value="/board/{boardIdx}", method=RequestMethod.PUT)
    public void updateBoard(@PathVariable("boardIdx") int boardIdx,@RequestBody BoardDto board) throws Exception {
        board.setBoardIdx(boardIdx);
        boardService.updateBoard(board);
    }

    @RequestMapping(value="/board/{boardIdx}", method=RequestMethod.DELETE)
    public void deleteBoard(@PathVariable("boardIdx") int boardIdx) throws Exception {
        BoardDto boardDto = new BoardDto();
        boardDto.setBoardIdx(boardIdx);
        boardService.deleteBoard(boardDto);
    }

    @RequestMapping(value="/board/file", method=RequestMethod.GET)
    public void downloadBoardFile(@RequestParam int idx, @RequestParam int boardIdx, HttpServletResponse response) throws Exception {
        BoardFileDto boardFile = boardService.selectBoardFileInfo(idx, boardIdx);

        if (!ObjectUtils.isEmpty(boardFile)) {
            String fileName = boardFile.getOriginalFileName();
            byte[] files = FileUtils.readFileToByteArray(new File(boardFile.getStoredFilePath()));

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
