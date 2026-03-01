package com.ninjasmoke.security.book;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {

  private Integer id;
  private String author;
  private String isbn;
}
