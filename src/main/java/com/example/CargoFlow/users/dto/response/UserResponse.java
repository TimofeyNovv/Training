package com.example.CargoFlow.users.dto.response;

import com.example.CargoFlow.users.entity.enums.UserRole;
import com.example.CargoFlow.users.entity.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    UUID id;
    String email;
    String fullName;
    String phone;
    UserRole role;
    UserStatus status;
    boolean emailVerified;
    UUID avatarFileId;
    Instant createdAt;
    Instant updatedAt;
}