package com.restapi.test.service;

import com.restapi.test.model.response.ApiResponseModel;
import org.springframework.stereotype.Service;

/**
 * api response 관련 처리를 하는 service
 * @author JENNI
 * @version 1.0
 * @since 2022.02.06
 */

@Service
public class ApiResponseService {
    public ApiResponseModel getApiResponse(String success, String status_msg, Object result) {
        return ApiResponseModel.builder()
                .success(success)
                .status_message(status_msg)
                .result(result)
                .build();
    }
}
