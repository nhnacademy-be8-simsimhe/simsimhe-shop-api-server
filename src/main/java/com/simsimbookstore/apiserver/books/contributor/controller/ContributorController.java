package com.simsimbookstore.apiserver.books.contributor.controller;

import com.simsimbookstore.apiserver.books.book.dto.PageResponse;
import com.simsimbookstore.apiserver.books.contributor.dto.ContributorRequestDto;
import com.simsimbookstore.apiserver.books.contributor.dto.ContributorResponseDto;
import com.simsimbookstore.apiserver.books.contributor.entity.Contributor;
import com.simsimbookstore.apiserver.books.contributor.mapper.ContributorMapper;
import com.simsimbookstore.apiserver.books.contributor.service.ContributorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class ContributorController {

    private final ContributorService contributorService;

    /**
     * 기여자 등록
     *
     * @param requestDto
     * @param bindingResult
     * @return
     */
    @PostMapping("/contributors")
    public ResponseEntity<?> saveContributor(@RequestBody @Valid ContributorRequestDto requestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }

        ContributorResponseDto responseDto = contributorService.createContributor(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 기여자 모두 조회 페이징 조회는아님
     *
     * @return
     */
    @GetMapping("/contributors/list")
    public ResponseEntity<List<ContributorResponseDto>> findAllContributors() {
        List<ContributorResponseDto> contributors = contributorService.getAllContributor();

        return ResponseEntity.status(HttpStatus.OK).body(contributors);
    }


    /**
     * 페이지별로 기여자 조회하기
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/contributors")
    public ResponseEntity<PageResponse<ContributorResponseDto>> getAllContributorPage(@RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "2") int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        PageResponse<ContributorResponseDto> response = contributorService.getAllContributors(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    /**
     * 기여자 삭제.연관되어있는 기여자들은 삭제가안됌 외래키 제약조건 때문에
     *
     * @param contributorId
     * @return
     */
    @DeleteMapping("/contributors/{contributorId}")
    public ResponseEntity<?> delete(@PathVariable("contributorId") Long contributorId) {
        contributorService.deleteContributor(contributorId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 기여자 단건 조회
     *
     * @param contributorId
     * @return
     */
    @GetMapping("/contributors/{contributorId}")
    public ResponseEntity<?> getContributor(@PathVariable("contributorId") Long contributorId) {
        Contributor contributor = contributorService.getContributer(contributorId);
        ContributorResponseDto response = ContributorMapper.toResponse(contributor);
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    /**
     * 기여자 수정
     * @param contributorId
     * @param contributorRequestDto
     * @param bindingResult
     * @return
     */
    @PutMapping("/contributors/{contributorId}")
    public ResponseEntity<?> update(@PathVariable("contributorId") Long contributorId,
                                    @RequestBody @Valid ContributorRequestDto contributorRequestDto,
                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }
        ContributorResponseDto responseDto = contributorService.updateContributor(contributorId, contributorRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


}
