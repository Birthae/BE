package com.birthae.be.config;

import com.birthae.be.common.dto.ResponseMessage;
import com.birthae.be.common.exception.BizRuntimeException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BizRuntimeException.class)
    @ResponseBody
    public ResponseEntity<ResponseMessage> handleBizRuntimeException(BizRuntimeException e) {
        log.error("BizRuntime Error: {}", e.getMessage(), e);

        int statusCode = HttpStatus.BAD_REQUEST.value();
        String resultMessage = "처리 중 오류가 발생하였습니다: " + e.getMessage();

        ResponseMessage responseMessage = ResponseMessage.builder()
                .statusCode(statusCode)
                .resultMessage(resultMessage)
                .detailMessage(e.getMessage())
                .build();

        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(statusCode));
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ResponseMessage> handleGeneralException(Exception e, HttpServletRequest request, HttpServletResponse response) {
        log.error("Unexpected Error: {}", e.getMessage(), e);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .resultMessage("서버 내부 오류가 발생했습니다.")
                .detailMessage(e.getMessage())
                .build();

        return new ResponseEntity<>(responseMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}