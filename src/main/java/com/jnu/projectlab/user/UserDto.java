package com.jnu.projectlab.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Integer id;
    private String username;
    private Integer age;
    private String career; // career로 수정했습니다.
    private String education;
    private Integer incomeLevel;
    private Integer regionId;
    private LocalDateTime createdAt;
}
