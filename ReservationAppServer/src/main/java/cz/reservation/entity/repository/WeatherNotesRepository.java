package cz.reservation.entity.repository;

import cz.reservation.entity.WeatherNotesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherNotesRepository extends JpaRepository<WeatherNotesEntity, Long> {
}
