package com.example.shopshoesspring.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class CommentForm {
    private Long newsId;
    private String comment;
}
