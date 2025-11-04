package cz.reservation.entity;

import cz.reservation.constant.Handedness;
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

    @Column
    private String note;


}
