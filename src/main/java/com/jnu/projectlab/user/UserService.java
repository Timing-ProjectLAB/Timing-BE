package com.jnu.projectlab.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 비밀번호 암호화를 위한 인코더

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 인코더 주입

    // 전체 사용자 조회
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 특정 사용자 조회
    public Optional<UserDto> findUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDto);
    }

    // 사용자 생성 메서드
    public UserDto saveUser(UserDto userDto) {
        User user = convertToEntity(userDto);
        user = userRepository.save(user);
        return convertToDto(user);
    }

    // 사용자 삭제 메서드
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


    // 사용자 ID 존재 여부 확인 메서드 추가
    public boolean existsByUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }

    // 회원가입용 메서드 추가 (스프링 시큐리티)
    public Long save(UserDto userDto) {

        // 아이디 중복 검사
        if (existsByUserId(userDto.getUserId())) {
            throw new DuplicatedUserException("이미 존재하는 아이디입니다.");
        }

        // 비밀번호 암호화
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        User user = convertToEntity(userDto);
        return userRepository.save(user).getId();
    }


    // 데이터 Read를 위한 서비스 코드 Entity → DTO
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUserId(user.getUserId()); // userId 필드 추가
        // 비밀번호는 보안을 위해 DTO에 포함하지 않음
        dto.setBirthDate(user.getBirthDate()); // birthDate 필드 추가
        dto.setGender(user.getGender()); // gender 필드 추가
        dto.setIncomeBracket(user.getIncomeBracket()); // incomeBracket 필드 추가
        dto.setRegionId(user.getRegionId());
        dto.setOccupation(user.getOccupation()); // occupation 필드 추가
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt()); // updatedAt 필드 추가
        return dto;
    }

    // 데이터 생성/수정을 위한 서비스 코드 (Dto -> Entity)
    private User convertToEntity(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUserId(dto.getUserId()); // userId 필드 추가

        // 비밀번호가 있을 경우에만 설정 (보안 강화)
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(dto.getPassword());
        }

        user.setBirthDate(dto.getBirthDate()); // birthDate 필드 추가
        user.setGender(dto.getGender()); // gender 필드 추가
        user.setIncomeBracket(dto.getIncomeBracket()); // incomeBracket 필드 추가
        user.setRegionId(dto.getRegionId());
        user.setOccupation(dto.getOccupation()); // occupation 필드 추가
        user.setCreatedAt(dto.getCreatedAt());
        user.setUpdatedAt(dto.getUpdatedAt()); // updatedAt 필드 추가
        return user;
    }
}