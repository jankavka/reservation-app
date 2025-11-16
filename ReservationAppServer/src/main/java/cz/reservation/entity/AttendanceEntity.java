package cz.reservation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceEntity {

    @Id
    @Column(name = "booking_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "booking_id")
    private BookingEntity booking;

    @Column
    private Boolean present;

    @Column
    private String note;
}
