package com.simsimbookstore.apiserver.books.category.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Table(
        name = "category",
        uniqueConstraints = @UniqueConstraint(columnNames = {"category_name", "parent_id"})
)

public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_name", nullable = false, length = 50)
    private String categoryName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    @ToString.Exclude
    private List<Category> children = new ArrayList<>(); // 자식 카테고리 목록

    public void addChildCategory(Category child) {
        this.children.add(child);
        child.setParent(this); // 자식의 부모도 설정
    }


}
