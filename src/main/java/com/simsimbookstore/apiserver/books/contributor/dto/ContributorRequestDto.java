package com.simsimbookstore.apiserver.books.contributor.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@EqualsAndHashCode
public class ContributorRequestDto {

    @NotBlank(message = "이름을 공백없이 입력해주세요")
    private String contributorName;

    @NotBlank(message = "역할을 입력해주세요 ex)기여자,그림,옮긴이")
    private String contributorRole;

}
