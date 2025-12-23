package cz.reservation.entity;

import cz.reservation.constant.WeatherCondition;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "weather_notes")
public class WeatherNotesEntity {

    @Id
    @Column(name = "training_slot_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "training_slot_id")
    private TrainingSlotEntity trainingSlot;

    @Column
    private WeatherCondition weatherCondition;

    @Column
    private String note;
}
