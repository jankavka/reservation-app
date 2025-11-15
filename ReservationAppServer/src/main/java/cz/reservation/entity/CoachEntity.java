package cz.reservation.entity;

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
public class CoachEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @PrimaryKeyJoinColumn(name = "user")
    @OneToOne
    private UserEntity userId;

    @Column
    private String bio;

    @Column
    private String certification;

    @OneToMany(mappedBy = "coach", cascade = CascadeType.ALL)
    @ElementCollection
    private List<GroupEntity> groups;




}
