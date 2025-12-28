package cz.reservation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "seasons")
public class SeasonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column(name = "date_from")
    private LocalDateTime dateFrom;

    @Column(name = "date_until")
    private LocalDateTime dateUntil;

    @OneToMany(mappedBy = "season",cascade = CascadeType.ALL)
    private List<GroupEntity> groups = new ArrayList<>();
}
