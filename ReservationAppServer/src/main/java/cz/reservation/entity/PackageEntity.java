package cz.reservation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

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

    @OneToMany(mappedBy = "packagee")
    private List<PlayerEntity> players;

    @Column
    private String name;

    @Column
    private Integer slotTotal;

    @Column
    private Integer slotUsed;

    @Column
    private LocalDate validFrom;

    @Column
    private LocalDate validTo;

    @Column
    private LocalDate generatedAt;

    @Column
    private String path;

    @ManyToOne
    @JoinColumn(name = "pricing_rule")
    private PricingRuleEntity pricingRule;

}
