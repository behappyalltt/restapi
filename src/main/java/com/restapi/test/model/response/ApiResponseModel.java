package com.restapi.test.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * api return 값을 담아 전달하는 model
 * @author JENNI
 * @version 1.0
 * @since 2022.02.06
 */

@Builder
@Data
public class ApiResponseModel {
    @ApiModelProperty(value="성공 여부: success/fail")
    private String success;
    
    @ApiModelProperty(value="응답 메세지")
    private String status_message;

    @ApiModelProperty(value="결과 데이터")
    private Object result;
}
