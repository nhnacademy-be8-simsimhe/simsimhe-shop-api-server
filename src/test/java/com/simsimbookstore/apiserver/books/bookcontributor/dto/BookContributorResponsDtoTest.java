package com.simsimbookstore.apiserver.books.bookcontributor.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookContributorResponsDtoTest {
    @Test
    void testDtoCreationWithConstructor() {
        // Given
        Long contributorId = 1L;
        String contributorName = "John Doe";
        String contributorRole = "Author";
        // When
        BookContributorResponsDto dto = BookContributorResponsDto.builder().contributorId(contributorId).contributorName(contributorName)
                .contributorRole(contributorRole).build();
        // Then
        assertEquals(contributorId, dto.getContributorId());
        assertEquals(contributorName, dto.getContributorName());
        assertEquals(contributorRole, dto.getContributorRole());
    }

    @Test
    void testDtoCreationWithBuilder() {
        // Given
        Long contributorId = 2L;
        String contributorName = "Jane Smith";
        String contributorRole = "Editor";
        // When
        BookContributorResponsDto dto = BookContributorResponsDto.builder()
                .contributorId(contributorId)
                .contributorName(contributorName)
                .contributorRole(contributorRole)
                .build();
        // Then
        assertEquals(contributorId, dto.getContributorId());
        assertEquals(contributorName, dto.getContributorName());
        assertEquals(contributorRole, dto.getContributorRole());
    }
}