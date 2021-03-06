package com.restapi.test.exception;

import com.restapi.test.model.response.ApiResponseModel;
import com.restapi.test.service.ApiResponseService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @Valid Exception이 발생했을 때 ApiResponseModel로 return 해주기 위해 ResponseEntityExceptionHandler를 상속받아 수정한 handler
 * @author JENNI
 * @version 1.0
 * @since 2022.02.09
 */

@ControllerAdvice   // 모든 영역에서 발생할 수 있는 예외를 잡아줌
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        ApiResponseService apiResponseService = new ApiResponseService();
        ApiResponseModel apiResponse = apiResponseService.getApiResponse("fail", ex.getBindingResult().getFieldError().getDefaultMessage(), "");
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
