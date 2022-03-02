package com.restapi.test.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 회원 정보를 담을 때 사용하는 dto
 * 회원가입 시 파라미터를 받는 dto
 * @author JENNI
 * @version 1.0
 * @since 2022.02.06
 */

@Builder
@Data
public class MemberDto {
    // 회원 Id
    private String userId;
    
    // 비밀번호
    private String password;
    
    // 회원 이름
    private String name;
    
    // 주민등록번호
    private String regNo;
}
