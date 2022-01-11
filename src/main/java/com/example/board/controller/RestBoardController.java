package com.example.board.controller;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.example.board.dto.BoardDto;
import com.example.board.dto.BoardFileDto;
import com.example.board.service.BoardService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RestBoardController {
    @Autowired
    BoardService boardService;

    /*
     * @RequestMapping 어노테이션에 주소만 입력하면 value 속성을 생략할 수 있음
     * RESTful 서비스에서는 주소와 요청 방식을 지정해야 하므로 value 속성과 method 속성을 기술해야 함
     *
     * @GetMapping, @PostMapping, @PutMapping, @DeleteMapping 어노테이션 사용도 가능
     */
    @RequestMapping(value="/board", method=RequestMethod.GET)
    public ModelAndView openBoardList() throws Exception {
        ModelAndView mv = new ModelAndView("/board/restBoardList");

        List<BoardDto> data = boardService.selectBoardList();
        mv.addObject("resultList", data);

        return mv;
    }

    @RequestMapping(value="/board/write", method=RequestMethod.GET)
    public String openBoardWrite() throws Exception {
        return "/board/restBoardWrite";
    }

    @RequestMapping(value="/board/write", method=RequestMethod.POST)
    public String insertBoard(BoardDto board, MultipartHttpServletRequest request) throws Exception {
        boardService.insertBoard(board, request);
        return "redirect:/board";
    }

    @RequestMapping(value="/board/{boardIdx}", method=RequestMethod.GET)
    public ModelAndView openBoardDetail(@PathVariable("boardIdx") int boardIdx) throws Exception {
        ModelAndView mv = new ModelAndView("/board/restBoardDetail");

        BoardDto data = boardService.selectBoardDetail(boardIdx);
        mv.addObject("resultDetail", data);

        return mv;
    }

    @RequestMapping(value="/board/{boardIdx}", method=RequestMethod.PUT)
    public String updateBoard(@PathVariable("boardIdx") int boardIdx, BoardDto board) throws Exception {
        boardService.updateBoard(board);
        return "redirect:/board";
    }

    @RequestMapping(value="/board/{boardIdx}", method=RequestMethod.DELETE)
    public String deleteBoard(@PathVariable("boardIdx") int boardIdx, BoardDto board) throws Exception {
        boardService.deleteBoard(board);
        return "redirect:/board";
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
