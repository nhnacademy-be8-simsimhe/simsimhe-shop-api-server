package com.simsimbookstore.apiserver.users.address.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.simsimbookstore.apiserver.common.config.QuerydslConfig;
import com.simsimbookstore.apiserver.users.address.entity.Address;
import com.simsimbookstore.apiserver.users.grade.entity.Grade;
import com.simsimbookstore.apiserver.users.grade.entity.Tier;
import com.simsimbookstore.apiserver.users.grade.repository.GradeRepository;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.entity.UserStatus;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import({QuerydslConfig.class})
@DataJpaTest
@ActiveProfiles("test")
class AddressRepositoryTest {
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GradeRepository gradeRepository;

    User testUser;
    Address testAddress1;
    Address testAddress2;

    @BeforeEach
    void setUp() {
        Grade standardGrade = Grade.builder()
                .tier(Tier.STANDARD)
                .minAmount(BigDecimal.valueOf(0))
                .maxAmount(BigDecimal.valueOf(100000))
                .build();

        gradeRepository.save(standardGrade);
        testUser = User.builder()
                .userName("John Doe")
                .email("johndoe@example.com")
                .createdAt(LocalDateTime.now())
                .userStatus(UserStatus.ACTIVE)
                .grade(standardGrade)
                .build();

        testUser = userRepository.save(testUser);

        testAddress1 = Address.builder()
                .user(testUser)
                .alias("자취방")
                .postalCode("61459")
                .roadAddress("광주광역시 동구 의재로 9")
                .detailedAddress("리더스빌 402호")
                .build();

        testAddress2 = Address.builder()
                .user(testUser)
                .alias("본집")
                .postalCode("21953")
                .roadAddress("인천광역시 연수구 청량로 210")
                .detailedAddress("쌍용아파트 102동 1602호")
                .build();

        testAddress1 = addressRepository.save(testAddress1);
        testAddress2 = addressRepository.save(testAddress2);
    }

    @Test
    @DisplayName("주소 단건 조회")
    void findById() {
        Optional<Address> optionalAddress = addressRepository.findById(testAddress1.getAddressId());
        assertTrue(optionalAddress.isPresent());
        assertEquals(testAddress1.getAddressId(), optionalAddress.get().getAddressId());
    }

    @Test
    @DisplayName("주소 리스트 조회")
    void findAllByUserUserId() {
        List<Address> addresses = addressRepository.findAllByUserUserId(testUser.getUserId());
        assertEquals(2, addresses.size());

        boolean result = addresses.stream().anyMatch(address -> address.getAlias().matches(testAddress1.getAlias()));
        assertTrue(result);

        result = addresses.stream().anyMatch(address -> address.getAlias().matches(testAddress2.getAlias()));
        assertTrue(result);
    }

    @Test
    @DisplayName("주소 삭제")
    void deleteAddress(){
        addressRepository.deleteById(testAddress1.getAddressId());
        Optional<Address> optionalAddress = addressRepository.findById(testAddress1.getAddressId());
        assertTrue(optionalAddress.isEmpty());
    }
}