package cz.reservation.entity;

import cz.reservation.constant.SlotStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "training_slots")
public class TrainingSlotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupEntity group;

    @ManyToOne
    @JoinColumn(name = "court_id")
    private CourtEntity court;

    @OneToMany(mappedBy = "trainingSlot")
    private List<BookingEntity> bookingEntities;

    @Column
    private LocalDateTime startAt;

    @Column
    private LocalDateTime endAt;

    @Column
    private Integer capacity;

    @Column
    private SlotStatus status;

    @Column
    private String price;

    @Column
    private String currency;

    @OneToOne(mappedBy = "trainingSlot", cascade = CascadeType.ALL)
    private WeatherNotesEntity weatherNotes;

    @OneToOne
    @JoinColumn(name = "court_blocking_id", referencedColumnName = "id")
    private CourtBlockingEntity courtBlocking;

}
