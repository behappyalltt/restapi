package com.restapi.test.repository;

import com.restapi.test.entity.MemberEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * member 테이블 repository
 * @author JENNI
 * @version 1.0
 * @since 2022.02.06
 */

public interface MemberRepository extends CrudRepository<MemberEntity, String> {
    Optional<MemberEntity> findByRegNo(String regNo);
}
