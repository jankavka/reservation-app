package cz.reservation.entity;

import cz.reservation.dto.TrainingSlotDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "court_blocks")
public class CourtBlockingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "court_id")
    private CourtEntity court;

    @Column
    private LocalDateTime blockedFrom;

    @Column
    private LocalDateTime blockedTo;

    @Column
    private String reason;

    @OneToOne(mappedBy = "courtBlocking", cascade = CascadeType.ALL)
    private TrainingSlotEntity trainingSlot;
}
