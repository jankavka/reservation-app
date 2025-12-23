package cz.reservation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "coaches")
public class CoachEntity  {

    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column
    private String bio;

    @Column
    private String certification;

    @OneToMany(mappedBy = "coach")
    private List<GroupEntity> groups;


    @Override
    public String toString(){
        return this.bio + " " + this.id;
    }




}
