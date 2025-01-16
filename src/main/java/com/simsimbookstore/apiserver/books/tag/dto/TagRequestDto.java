package com.simsimbookstore.apiserver.books.tag.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagRequestDto {

    @NotBlank(message = "태그이름을 공백없이 입력해주세요")
    @Length(max = 10,message = "최대 10글자입니다")
    private String tagName;

}
