package cz.reservation.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import cz.reservation.constant.Surface;
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
public class CourtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    String name;

    @Column
    Surface surface;

    @Column
//    @JsonDeserialize(using = NumberDeserializers.BooleanDeserializer.class)
    Boolean indoor;

    @Column
//    @JsonDeserialize(using = NumberDeserializers.BooleanDeserializer.class)
    Boolean lighting;

}
