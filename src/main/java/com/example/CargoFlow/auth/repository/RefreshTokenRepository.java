package com.example.CargoFlow.auth.repository;


import com.example.CargoFlow.auth.entity.RefreshTokenEntity;
import com.example.CargoFlow.users.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> getFirstByUser(UserEntity user);
    Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);

    void deleteAllByUser(UserEntity user);
}
