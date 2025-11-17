package cz.reservation.entity;

import cz.reservation.constant.Handedness;
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
@Table(name = "players")
public class PlayerEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String fullName;

    @Column
    private Date birthDate;

    @Column
    private Handedness handedness;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private UserEntity parent;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    private List<EnrollmentEntity> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    private List<BookingEntity> bookings = new ArrayList<>();

    @Column
    private String note;


}
