package com.example.board.common;

import com.example.board.dto.BoardDto;
import com.example.board.dto.BoardFileDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class FileUtils {
    public List<BoardFileDto> parseFileInfo(int boardIdx, MultipartHttpServletRequest request) throws Exception {
        // request 존재 여부 확인
        if (ObjectUtils.isEmpty(request)) {
            return null;
        }

        // 업로드 파일의 정보를 저장해서 반환할 리스트 객체를 선언
        List<BoardFileDto> fileInfoList = new ArrayList();

        // 파일 저장 디렉터리를 지정(없는 경우 생성)
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        ZonedDateTime now = ZonedDateTime.now();
        String storedDir = "images/" + now.format(dtf);	// images/20220111
        File fileDir = new File(storedDir);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        // <input type="file" name="여기의값을추출">
        Iterator<String> fileTagNames = request.getFileNames();
        while(fileTagNames.hasNext()) {

            // 업로드 파일의 Content Type에 맞춰서 지정할 확장자를 저장하는 변수
            String originalFileExtension;

            List<MultipartFile> files = request.getFiles(fileTagNames.next()); //파일 뭉텅이
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String contentType = file.getContentType();
                    if (ObjectUtils.isEmpty(contentType)) {
                        break;
                    } else {
                        if (contentType.contains("image/jpeg")) {
                            originalFileExtension = ".jpg";
                        } else if (contentType.contains("image/png")) {
                            originalFileExtension = ".png";
                        } else if (contentType.contains("image/gif")) {
                            originalFileExtension = ".gif";
                        } else {
                            break;
                        }
                    }

                    // 업로드 파일(원본 파일) 이름을 사용하지 않고, 현재 시간을 파일 이름으로 사용
                    // 시스템 외부에서 업로드 파일에 어디에, 어떤 형태로 저장되는지 모르도록 하기 위함
                    String storedFileName = Long.toString(System.nanoTime()) + originalFileExtension;

                    // DTO 작성 후 정보 넣고
                    BoardFileDto boardFile = new BoardFileDto();
                    boardFile.setIdx(boardIdx);
                    boardFile.setBoardIdx(boardIdx);
                    boardFile.setOriginalFileName(file.getOriginalFilename());
                    boardFile.setStoredFilePath(storedDir + "/" + storedFileName);
                    boardFile.setFileSize(file.getSize());

                    //리스트에 추가
                    fileInfoList.add(boardFile);

                    // 파일을 저장
                    fileDir = new File(storedDir + "/" + storedFileName);
                    file.transferTo(fileDir); //개별 파일들 옮김
                }
            }
        }

        return fileInfoList;
    }

}
