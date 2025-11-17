package cz.reservation.entity.repository;

import cz.reservation.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {

    List<PlayerEntity> findByParentId(Long userId);
}
