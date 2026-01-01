package cz.reservation.entity;

import cz.reservation.constant.Handedness;
import cz.reservation.constant.PricingType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private LocalDateTime birthDate;

    @Column
    private Handedness handedness;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private UserEntity parent;

    @Column
    private PricingType pricingType;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    private List<EnrollmentEntity> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceSummaryEntity> invoices = new ArrayList<>();

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    private List<BookingEntity> bookings = new ArrayList<>();

    @Column
    private String note;

    @ManyToOne
    @JoinColumn(name = "package")
    private PackageEntity packagee;


}
