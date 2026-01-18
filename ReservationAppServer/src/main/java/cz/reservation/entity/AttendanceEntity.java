package cz.reservation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "attendances")
public class AttendanceEntity {

    @Id
    @Column(name = "booking_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "booking_id", unique = true)
    private BookingEntity booking;

    @Column
    private Boolean present;

    @Column
    private String note;

    @Column
    private LocalDateTime createdAt;
}
