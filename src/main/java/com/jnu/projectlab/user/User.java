package com.jnu.projectlab.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails { // UserDetails를 상속받아 인증 객체로 사용


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // bigserial에 맞춰 Long으로 수정(PostSQL DB에 사용되는 데이터타입)

    @Column(name = "user_id", unique = true, nullable = false, length = 50)
    private String userId; // 사용자 로그인 ID

    @Column(nullable = false, length = 100)
    private String password; // BCryptPasswordEncoder로 해시된 비밀번호

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate; // 생년월일

    @Column(nullable = false)
    private Integer gender; // 성별 (1: 남성, 0: 여성)

    @Column(name = "income_bracket")
    private Integer incomeBracket; // 소득 수준 (1-10 스케일)

    @Column(name = "region_id", nullable = false)
    private Integer regionId; // 사용자 거주 지역 ID

    @Column(length = 100)
    private String occupation; // 직업 정보

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 생성 시간

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정 시간

    @PrePersist
    protected void onCreate() { // 시간을 실시간으로 전달받기 위해 사용
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() { //시간을 실시간으로 전달받기 위해 사용
        updatedAt = LocalDateTime.now();
    }


    // UserDetails 인터페이스 구현 메서드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return this.userId; // 스프링 시큐리티에서 사용자 식별자로 userId 필드 사용
    }

    @Override
    public boolean isAccountNonExpired() {
        // 계정 만료 여부 반환
        return true; // true -> 만료되지 않음
    }

    @Override
    public boolean isAccountNonLocked() {
        // 계정 잠금 여부 반환
        return true; // true -> 잠금되지 않음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 패스워드 만료 여부 반환
        return true; // true -> 만료되지 않음
    }

    @Override
    public boolean isEnabled() {
        // 계정 사용 가능 여부 변환
        return true; // true -> 사용 가능
    }
}