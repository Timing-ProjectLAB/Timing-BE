package com.jnu.projectlab.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 전체 사용자 조회
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 특정 사용자 조회
    public Optional<UserDto> findUserById(Integer id) {
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
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    // 데이터 Read를 위한 서비스 코드  Entity → DTO
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setAge(user.getAge());
        dto.setCareer(user.getCareer());
        dto.setEducation(user.getEducation());
        dto.setIncomeLevel(user.getIncomeLevel());
        dto.setRegionId(user.getRegionId());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    // 데이터 생성/수정을 위한 서비스 코드 (Dto -> Entity)
    private User convertToEntity(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setAge(dto.getAge());
        user.setCareer(dto.getCareer());
        user.setEducation(dto.getEducation());
        user.setIncomeLevel(dto.getIncomeLevel());
        user.setRegionId(dto.getRegionId());
        user.setCreatedAt(dto.getCreatedAt());
        return user;
    }
}