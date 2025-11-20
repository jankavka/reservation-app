package cz.reservation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "groups")
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "coach_id")
    private CoachEntity coach;

    @ManyToOne
    @JoinColumn(name = "season_id")
    private SeasonEntity season;

    /**
     * Consider with diff cascade type, because when we delete group
     * we can set enrollment to EnrollmentState.CANCELED and persist
     * EnrollmentEntity in db.
     */
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<EnrollmentEntity> enrollments = new ArrayList<>();

    @Column
    private Integer capacity;
}
