package cz.reservation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "packages")
public class PackageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "packagee")
    private PlayerEntity player;

    @Column
    private String name;

    @Column
    private Integer availableSlots;

    @Column
    private LocalDate validFrom;

    @Column
    private LocalDate validTo;

    @Column
    private LocalDate generatedAt;

    @Column
    private String path;


}
