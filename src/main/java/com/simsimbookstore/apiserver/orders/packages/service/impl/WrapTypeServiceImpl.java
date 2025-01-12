package com.simsimbookstore.apiserver.orders.packages.service.impl;

import com.simsimbookstore.apiserver.orders.packages.dto.WrapTypeRequestDto;
import com.simsimbookstore.apiserver.orders.packages.dto.WrapTypeResponseDto;
import com.simsimbookstore.apiserver.orders.packages.entity.WrapType;
import com.simsimbookstore.apiserver.orders.packages.exception.WrapTypeNotFoundException;
import com.simsimbookstore.apiserver.orders.packages.repository.WrapTypeRepository;
import com.simsimbookstore.apiserver.orders.packages.service.WrapTypeService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class WrapTypeServiceImpl implements WrapTypeService {

    private final WrapTypeRepository wrapTypeRepository;

    public WrapTypeServiceImpl(WrapTypeRepository wrapTypeRepository) {
        this.wrapTypeRepository = wrapTypeRepository;
    }

    @Override
    public WrapTypeResponseDto createWrapType(WrapTypeRequestDto wrapTypeRequestDto) {
        WrapType wrapType = wrapTypeRequestDto.toEntity();
        WrapType savedWrapType = wrapTypeRepository.save(wrapType);
        return savedWrapType.toResponseDto();
    }

    @Override
    public WrapTypeResponseDto getWrapTypeById(Long id) {
        return wrapTypeRepository.findById(id)
                .map(WrapType::toResponseDto)
                .orElseThrow(() -> new WrapTypeNotFoundException("WrapType not found with ID: " + id));
    }

    @Override
    public List<WrapTypeResponseDto> getAllWrapTypes() {
        return wrapTypeRepository.findAll().stream()
                .map(WrapType::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public WrapTypeResponseDto updateAvailability(Long id, Boolean isAvailable) {
        WrapType wrapType = wrapTypeRepository.findById(id)
                .orElseThrow(() -> new WrapTypeNotFoundException("WrapType not found with ID: " + id));

        wrapType.updateAvailability(isAvailable);

        WrapType updatedWrapType = wrapTypeRepository.save(wrapType);

        return updatedWrapType.toResponseDto();
    }

    @Override
    public List<WrapTypeResponseDto> getAllWarpTypeIsAvailable() {
        return wrapTypeRepository.findAllByIsAvailableTrue()
                .stream().map(WrapType::toResponseDto)
                .collect(Collectors.toList());
    }
}
