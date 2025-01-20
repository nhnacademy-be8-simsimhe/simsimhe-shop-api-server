package com.simsimbookstore.apiserver.orders.packages.controller;

import com.simsimbookstore.apiserver.orders.packages.dto.WrapTypeRequestDto;
import com.simsimbookstore.apiserver.orders.packages.dto.WrapTypeResponseDto;
import com.simsimbookstore.apiserver.orders.packages.service.WrapTypeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/wrap-types")
public class WrapTypeController {

    private final WrapTypeService wrapTypeService;

    public WrapTypeController(WrapTypeService wrapTypeService) {
        this.wrapTypeService = wrapTypeService;
    }

    /**
     * wrapType 포장지 종류 조회
     * @param id WrapType의 PK
     * @return
     */

    @GetMapping("/{id}")
    public ResponseEntity<WrapTypeResponseDto> getWrapTypeById(@PathVariable Long id) {
        WrapTypeResponseDto wrapType = wrapTypeService.getWrapTypeById(id);
        return ResponseEntity.ok(wrapType);
    }

    /**
     *
     * @param wrapTypeRequestDto 포장지를 저장 요청 dto
     * @return
     */

    @PostMapping
    public ResponseEntity<WrapTypeResponseDto> createWrapType(@RequestBody @Valid WrapTypeRequestDto wrapTypeRequestDto) {
        WrapTypeResponseDto createdWrapType = wrapTypeService.createWrapType(wrapTypeRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWrapType);
    }

    /**
     * 모든 포장지를 확인함
     * @return
     */


    @GetMapping
    public ResponseEntity<List<WrapTypeResponseDto>> getAllWrapTypes() {
        List<WrapTypeResponseDto> wrapTypes = wrapTypeService.getAllWrapTypes();
        return ResponseEntity.ok(wrapTypes);
    }

    /**
     *
     * @param id 판매가능여부를 수정할 포장지타입의 id
     * @param isAvailable 불, 가 를 바꿀 boolean
     * @return
     */

    @PostMapping("/{id}/availability")
    public ResponseEntity<WrapTypeResponseDto> updateAvailability(
            @PathVariable Long id,
            @RequestParam Boolean isAvailable) {

        WrapTypeResponseDto updatedWrapType = wrapTypeService.updateAvailability(id, isAvailable);
        return ResponseEntity.ok(updatedWrapType);
    }
}
