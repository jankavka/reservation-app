package cz.reservation.entity;


import cz.reservation.constant.PricingType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.Month;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "invoice_summaries")
public class InvoiceSummaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column
    private Month month;

    @Column
    private Integer totalCentsAmount;

    @Column
    private PricingType pricingType;

    @Column
    private String currency;

    @Column
    private LocalDateTime generatedAt;

    @Column
    private String path;


}
