package com.restapi.test.service;

import com.google.gson.Gson;
import com.restapi.test.dto.MemberDto;
import com.restapi.test.dto.MemberJoinDto;
import com.restapi.test.dto.MemberLoginDto;
import com.restapi.test.entity.MemberEntity;
import com.restapi.test.model.response.ApiResponseModel;
import com.restapi.test.model.response.TokenInfoModel;
import com.restapi.test.repository.MemberRepository;
import com.restapi.test.security.AES256;
import com.restapi.test.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * member 관련 처리를 하는 service
 * @author JENNI
 * @version 1.0
 * @since 2022.02.06
 */

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ApiResponseService apiResponseService;

    // 회원가입
//    public String join(MemberJoinDto member) {
//        ApiResponseModel apiResponse = null;
//        try {
//            // password 암호화: BCrypt Hashing
//            member.setPassword(passwordEncoder.encode(member.getPassword()));
//            // 주민등록번호 암호화: AES256
//            member.setRegNo((new AES256()).encrypt(member.getRegNo()));
//
//            // 회원 Id 중복 체크
//            if(!validateUserId(member.getUserId())) {
//                MemberDto memberDto = MemberDto.builder()
//                        .userId(member.getUserId())
//                        .build();
//                apiResponse = apiResponseService.getApiResponse("fail", "이미 가입된 Id 입니다.", memberDto);
//            }
//            // 주민등록번호 중복 체크
//            else if(!validateRegNo(member.getRegNo())) {
//                apiResponse = apiResponseService.getApiResponse("fail", "이미 가입된 주민등록번호 입니다.", "");
//            }
//            // 가입 가능한 정보
//            else {
//                MemberEntity newMember = memberRepository.save(MemberEntity.builder()
//                        .userId(member.getUserId())
//                        .password(member.getPassword())
//                        .name(member.getName())
//                        .regNo(member.getRegNo())
//                        .build());
//
//                MemberDto memberDto = MemberDto.builder()
//                        .userId(newMember.getUserId())
//                        .build();
//
//                //apiResponse = apiResponseService.getApiResponse("success", "", memberDto);
//            }
//        } catch (Exception e) {
//            apiResponse = apiResponseService.getApiResponse("fail", "회원가입에 실패하였습니다.", "");
//        }
//
//        return (new Gson()).toJson(apiResponse);
//    }

    @Transactional(rollbackFor = Exception.class)
    public String join(MemberJoinDto member) throws Exception {
        ApiResponseModel apiResponse = null;
        // password 암호화: BCrypt Hashing
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        // 주민등록번호 암호화: AES256
        member.setRegNo((new AES256()).encrypt(member.getRegNo()));

        // 회원 Id 중복 체크
        if (!validateUserId(member.getUserId())) {
            MemberDto memberDto = MemberDto.builder()
                    .userId(member.getUserId())
                    .build();
            apiResponse = apiResponseService.getApiResponse("fail", "이미 가입된 Id 입니다.", memberDto);
        }
        // 주민등록번호 중복 체크
        else if (!validateRegNo(member.getRegNo())) {
            apiResponse = apiResponseService.getApiResponse("fail", "이미 가입된 주민등록번호 입니다.", "");
        }
        // 가입 가능한 정보
        else {
            MemberEntity newMember = save(MemberEntity.builder()
                    .userId(member.getUserId())
                    .password(member.getPassword())
                    .name(member.getName())
                    .regNo(member.getRegNo())
                    .build());

            MemberDto memberDto = MemberDto.builder()
                    .userId(newMember.getUserId())
                    .build();

            apiResponse = apiResponseService.getApiResponse("success", "", memberDto);
        }

        return (new Gson()).toJson(apiResponse);
    }

    // 로그인
    public String login(MemberLoginDto loginInfo) {
        ApiResponseModel apiResponse = null;
        MemberEntity member = null;

        try {
            // 전달 받은 Id로 회원 조회
            Optional<MemberEntity> memberEntity = memberRepository.findById(loginInfo.getUserId());

            // 일치하는 Id가 없을 때
            if(memberEntity.isEmpty()) {
                apiResponse = apiResponseService.getApiResponse("fail", "가입되지 않은 Id 입니다.", "");
            }
            // 일치하는 Id가 있을 때
            else {
                member = memberEntity.get();
                // 비밀번호가 틀렸을 때
                if (!passwordEncoder.matches(loginInfo.getPassword(), member.getPassword())) {
                    apiResponse = apiResponseService.getApiResponse("fail", "잘못된 비밀번호입니다.", "");
                }
                // 비밀번호가 일치했을 때
                else {
                    // 로그인 성공 시 token 발행 후 결과로 전송할 TokenInfoDto에 저장
                    TokenInfoModel jwtToken = TokenInfoModel.builder()
                                    .userId(member.getUserId())
                                    .token(jwtTokenProvider.createToken(member.getUserId()))
                                    .build();
                    apiResponse = apiResponseService.getApiResponse("success", "", jwtToken);
                }
            }
        } catch (Exception e) {
            apiResponse = apiResponseService.getApiResponse("fail", "로그인에 실패하였습니다.", "");
        }

        return (new Gson()).toJson(apiResponse);
    }

    // 회원 정보 가져오기
    public MemberDto getMemberInfo(String userId) {
        try {
            MemberEntity member = memberRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 Id 입니다."));

            return MemberDto.builder()
                    .userId(member.getUserId())
                    .name(member.getName())
                    .regNo((new AES256()).decrypt(member.getRegNo()))
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

    // 회원 Id 중복 체크
    public boolean validateUserId(String userId) {
        Optional<MemberEntity> member = memberRepository.findById(userId);
        if(member.isEmpty()) return true;
        else return false;
    }

    // 회원 주민등록번호 중복 체크
    public boolean validateRegNo(String regNo) {
        Optional<MemberEntity> member = memberRepository.findByRegNo(regNo);
        if(member.isEmpty()) return true;
        else return false;
    }

    public MemberEntity save(MemberEntity member) {
        return memberRepository.save(member);
    }
}
