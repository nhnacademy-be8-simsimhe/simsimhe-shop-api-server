package com.simsimbookstore.apiserver.orders.packages.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.simsimbookstore.apiserver.orders.packages.entity.WrapType;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class WrapTypeRepositoryTest {

    @Autowired
    private WrapTypeRepository wrapTypeRepository;

    @Test
    void testCreateAndRetrieveWrapType() {
        WrapType wrapType = WrapType.builder()
                .packageName("Test Package")
                .packagePrice(BigDecimal.valueOf(15000))
                .isAvailable(true)
                .build();

        WrapType savedWrapType = wrapTypeRepository.save(wrapType);
        WrapType retrievedWrapType = wrapTypeRepository.findById(savedWrapType.getPackageTypeId()).orElse(null);

        assertNotNull(retrievedWrapType);
        assertEquals("Test Package", retrievedWrapType.getPackageName());
        assertEquals(BigDecimal.valueOf(15000), retrievedWrapType.getPackagePrice());
        assertTrue(retrievedWrapType.getIsAvailable());
    }

    @Test
    void testUpdateAvailability() {
        WrapType wrapType = WrapType.builder()
                .packageName("Updatable Package")
                .packagePrice(BigDecimal.valueOf(1500))
                .isAvailable(true)
                .build();
        WrapType savedWrapType = wrapTypeRepository.save(wrapType);

        savedWrapType.updateAvailability(false);
        wrapTypeRepository.save(savedWrapType);

        WrapType updatedWrapType = wrapTypeRepository.findById(savedWrapType.getPackageTypeId()).orElse(null);

        assertNotNull(updatedWrapType);
        assertFalse(updatedWrapType.getIsAvailable());
    }

    @Test
    void testFindAllWrapTypes() {
        WrapType wrapType1 = WrapType.builder()
                .packageName("Package 1")
                .packagePrice(BigDecimal.valueOf(1000))
                .isAvailable(true)
                .build();

        WrapType wrapType2 = WrapType.builder()
                .packageName("Package 2")
                .packagePrice(BigDecimal.valueOf(2000))
                .isAvailable(false)
                .build();

        wrapTypeRepository.save(wrapType1);
        wrapTypeRepository.save(wrapType2);

        List<WrapType> wrapTypes = wrapTypeRepository.findAll();

        assertEquals(2, wrapTypes.size());
    }
}
