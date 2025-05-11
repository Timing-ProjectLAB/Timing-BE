package com.jnu.projectlab.user;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id; // User 엔티티와 타입을 맞추기 위해 Integer에서 Long으로 변경
    private String userId; // 로그인에 사용할 사용자 ID 추가
    private String password; // 비밀번호 필드 추가
    private LocalDate birthDate; // 생년월일 필드 추가
    private Integer gender; // 성별 필드 추가
    private Integer incomeBracket; // incomeLevel에서 이름 변경 (엔티티 필드명과 일치)
    private Integer regionId;
    private String occupation; // 직업 정보 필드 추가
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt; // 수정 시간 필드 추가
}