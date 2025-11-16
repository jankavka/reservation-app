package cz.reservation.entity;

import cz.reservation.constant.SlotStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @Column
    private Date startAt;

    @Column
    private Date endAt;

    @Column
    private Integer capacity;

    @Column
    private SlotStatus status;

    @Column
    private String price;

    @Column
    private String currency;

    @OneToOne(mappedBy = "trainingSlot")
    @PrimaryKeyJoinColumn
    private WeatherNotesEntity weatherNotes;
}
