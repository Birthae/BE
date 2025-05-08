package com.birthae.be.config;

import com.birthae.be.common.dto.ResponseMessage;
import com.birthae.be.common.exception.BizRuntimeException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionHandlingFilter extends OncePerRequestFilter {

    @Autowired
    private com.birthae.be.config.GlobalExceptionHandler exceptionHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (BizRuntimeException e) {
            ResponseEntity<ResponseMessage> responseEntity = exceptionHandler.handleBizRuntimeException(e);
            writeJsonResponse(response, responseEntity);
        } catch (Exception e) {
            ResponseEntity<ResponseMessage> responseEntity = exceptionHandler.handleGeneralException(e, request, response);
            writeJsonResponse(response, responseEntity);
        }
    }

    private void writeJsonResponse(HttpServletResponse response, ResponseEntity<ResponseMessage> responseEntity)
            throws IOException {
        response.setStatus(responseEntity.getStatusCodeValue());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseEntity.getBody()));
    }
}