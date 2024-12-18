package com.simsimbookstore.apiserver.books.book.entity;


import lombok.*;

@Getter
public enum BookStatus {
    ONSALE("정상판매"),
    SOLDOUT("매진"),
    OUTOFPRINT("절판"),
    DELETED("삭제");

    private final String status;


    BookStatus(String status) {
        this.status = status;
    }
}
