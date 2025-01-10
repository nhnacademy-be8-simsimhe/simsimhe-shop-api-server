package com.simsimbookstore.apiserver.carts.service;

import com.simsimbookstore.apiserver.books.book.entity.Book;
import com.simsimbookstore.apiserver.books.book.repository.BookRepository;
import com.simsimbookstore.apiserver.carts.entity.Cart;
import com.simsimbookstore.apiserver.carts.dto.CartRequestDto;
import com.simsimbookstore.apiserver.carts.dto.CartResponseDto;
import com.simsimbookstore.apiserver.carts.repository.CartRepository;
import com.simsimbookstore.apiserver.common.exception.NotFoundException;
import com.simsimbookstore.apiserver.exception.BadRequestException;
import com.simsimbookstore.apiserver.users.user.entity.User;
import com.simsimbookstore.apiserver.users.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public CartResponseDto getBookForCart(String bookId, int quantity) {
        CartResponseDto responseDto = cartRepository.getBookForCart(Long.valueOf(bookId));
        responseDto.setQuantity(quantity); //장바구니에 담으려는 수량

        if (responseDto.getBookQuantity() < quantity) {
            throw new BadRequestException("남은 재고보다 담는 양이 더 많습니다.");
        }

        return responseDto;
    }

    /**
     * 레디스에있는  장바구니 도서들 DB에 넣기 (프론트쪽에서 로그아웃됐을떄)
     *
     * @param userId
     * @param cartRequestDtoList
     */
//    public void CartToDb(Long userId, List<CartRequestDto> cartRequestDtoList) {
//        //장바구니 비우기
//        cartRepository.deleteByUserId(userId);
//
//        Optional<User> optionalUser = userRepository.findById(userId);
//
//        if (optionalUser.isPresent()) {
//            User user = optionalUser.get();
//
//            List<Cart> cartList = cartRequestDtoList.stream()
//                    .map(c -> {
//                        Optional<Book> optionalBook = bookRepository.findById(Long.valueOf(c.getBookId()));
//                        if (optionalBook.isPresent()) {
//                            Book book = optionalBook.get();
//                            return Cart.builder()
//                                    .user(user)
//                                    .book(book)
//                                    .quantity(c.getQuantity())
//                                    .build();
//                        } else {
//                            throw new NotFoundException("도서 정보가 없습니다");
//                        }
//                    }).collect(Collectors.toList());
//            cartRepository.saveAll(cartList);
//        }else {
//            throw new NotFoundException("회원 정보가 없습니다");
//        }
//    }
    public void CartToDb(Long userId, List<CartRequestDto> cartRequestDtoList) {
        // 장바구니 비우기
        cartRepository.deleteByUserId(userId);

        // 사용자 엔터티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("회원 정보가 없습니다: " + userId));

        // 장바구니 항목 생성
        List<Cart> cartList = cartRequestDtoList.stream()
                .map(c -> {
                    Book book = bookRepository.findById(Long.valueOf(c.getBookId()))
                            .orElseThrow(() -> new NotFoundException("도서 정보가 없습니다: " + c.getBookId()));

                    return Cart.builder()
                            .user(user) // 조회된 User 엔터티 사용
                            .book(book)
                            .quantity(c.getQuantity())
                            .build();
                })
                .collect(Collectors.toList());

        // 장바구니 저장
        cartRepository.saveAll(cartList);
    }

}
