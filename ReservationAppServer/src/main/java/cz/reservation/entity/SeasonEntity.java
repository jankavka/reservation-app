package cz.reservation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
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
    private Date dateFrom;

    @Column(name = "date_until")
    private Date dateUntil;

    @OneToMany(mappedBy = "season",cascade = CascadeType.ALL)
    @ElementCollection
    private List<GroupEntity> groups = new ArrayList<>();
}
