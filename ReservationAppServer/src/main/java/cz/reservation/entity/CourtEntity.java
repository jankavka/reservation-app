package cz.reservation.entity;

import cz.reservation.constant.Surface;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "courts")
public class CourtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private Surface surface;

    @Column
    private Boolean indoor;

    @Column
    private Boolean lighting;

    @OneToMany(mappedBy = "court")
    private List<CourtBlockingEntity> blocks;

    @ManyToOne
    @JoinColumn(name = "venue_id")
    private VenueEntity venue;


}
