package com.simsimbookstore.apiserver.point.controller;

import com.simsimbookstore.apiserver.point.dto.PointPolicyRequestDto;
import com.simsimbookstore.apiserver.point.dto.PointPolicyResponseDto;
import com.simsimbookstore.apiserver.point.service.PointPolicyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/pointPolicies")
public class PointPolicyController {

    private final PointPolicyService pointPolicyService;

    /**
     * 모든 포인트 정책 조회
     */
    @GetMapping
    public ResponseEntity<List<PointPolicyResponseDto>> getAll() {
        List<PointPolicyResponseDto> allPolicies = pointPolicyService.getAllPolicies();
        return ResponseEntity.ok(allPolicies);
    }

    @GetMapping("/{policyId}")
    public ResponseEntity<PointPolicyResponseDto> getById(@PathVariable long policyId) {
        return ResponseEntity.ok(pointPolicyService.getPolicyById(policyId));
    }

    /**
     * 정책 생성
     */
    @PostMapping
    public ResponseEntity<PointPolicyResponseDto> create(@RequestBody PointPolicyRequestDto requestDto) {
        PointPolicyResponseDto created = pointPolicyService.createPolicy(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * 정책 수정
     */

    @PostMapping("/{policyId}")
    public ResponseEntity<PointPolicyResponseDto> updatePolicy(
            @PathVariable Long policyId,
            @RequestBody PointPolicyRequestDto requestDto) {
        PointPolicyResponseDto updated = pointPolicyService.updatePolicy(policyId, requestDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * 정책 삭제
     */
    @DeleteMapping("/{policyId}")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long policyId) {
        pointPolicyService.deletePolicy(policyId);
        return ResponseEntity.noContent().build();
    }
}
