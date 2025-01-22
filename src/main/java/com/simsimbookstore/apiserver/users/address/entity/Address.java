package com.simsimbookstore.apiserver.users.address.entity;


import com.simsimbookstore.apiserver.users.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Table(name = "addresses")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 10)
    private String alias;

    @Column(name = "postal_code", nullable = false, length = 5)
    private String postalCode;

    @Lob
    @Column(name = "road_address", nullable = false)
    private String roadAddress;

    @Column(name = "detailed_address", nullable = false)
    private String detailedAddress;

    public void assignUser(User user) {
        this.user = user;
    }
}
