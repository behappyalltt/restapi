package com.restapi.test.controller;

import com.google.gson.Gson;
import com.restapi.test.dto.MemberDto;
import com.restapi.test.dto.MemberJoinDto;
import com.restapi.test.dto.MemberLoginDto;
import com.restapi.test.model.response.ApiResponseModel;
import com.restapi.test.security.JwtTokenProvider;
import com.restapi.test.service.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * rest api를 처리하는 컨트롤러
 * @author JENNI
 * @version 1.0
 * @since 2022.02.09
 */

@RequiredArgsConstructor    // 생성자 주입
@RestController             // RESTful 웹 서비스에서 사용하는 컨트롤러(@Controller + @ResponseBody)
public class RestApiController {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;
    private final ApiResponseService apiResponseService;

    @ApiOperation(value="회원가입", notes="")
    @PostMapping("/v1/signup")
    public String signup(@RequestBody @ApiParam(value="회원가입할 때 필요한 회원 정보", required = true) @Valid MemberJoinDto member) {
        try {
            return memberService.join(member);
        } catch (Exception e) {
            return (new Gson()).toJson(apiResponseService.getApiResponse("fail", "회원가입에 실패하였습니다.", ""));
        }
    }

    @ApiOperation(value="로그인", notes="userId와 password로 로그인, jwt token 발급")
    @PostMapping("/v1/login")
    public String login(@RequestBody @ApiParam(value="로그인할 때 필요한 회원 정보", required = true) @Valid MemberLoginDto member) {
        return memberService.login(member);
    }

    @ApiOperation(value="내 정보 보기", notes="jwt token을 받아 권한이 있는 회원만 본인의 정보 열람 가능")
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-AUTH-TOKEN", value="로그인 성공 후 발급 받은 token", dataType="String", paramType="header", required=true)

    })
    @PostMapping("/v1/me")
    public String me(HttpServletRequest request) {
        String jwtToken = jwtTokenProvider.resolveToken(request);
        ApiResponseModel apiResponse = null;

        // token 확인 후 유효한 token일 때
        if(jwtTokenProvider.validateToken(jwtToken)) {
            String userId = jwtTokenProvider.getUserPk(jwtToken);
            MemberDto member = memberService.getMemberInfo(userId);

            if(member == null) apiResponse = apiResponseService.getApiResponse("fail", "회원 정보 조회에 실패하였습니다.", "");
            else apiResponse = apiResponseService.getApiResponse("success", "", member);
        }
        // token이 유효하지 않을 때
        else {
            apiResponse = apiResponseService.getApiResponse("fail", "token이 유효하지 않습니다.", "");
        }

        return (new Gson()).toJson(apiResponse);
    }
}
