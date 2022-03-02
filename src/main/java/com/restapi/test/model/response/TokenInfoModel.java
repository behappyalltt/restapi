package com.restapi.test.model.response;

import lombok.Builder;
import lombok.Data;

/**
 * 로그인 성공 시 token 값을 담아 return하는 model
 * @author JENNI
 * @version 1.0
 * @since 2022.02.06
 */

@Builder
@Data
public class TokenInfoModel {
    // 회원 Id
    private String userId;

    // jwt token
    private String token;
}
