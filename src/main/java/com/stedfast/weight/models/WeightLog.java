package com.stedfast.weight.models;

import com.stedfast.user.models.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import de.fxlae.typeid.TypeId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "weightLog")
@Data
@NoArgsConstructor
public class WeightLog {

    @Id
    @Column(length = 31)
    private String id;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = TypeId.generate("weight").toString();
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal weight;

    @CreationTimestamp
    @Column(name = "loggedTime", insertable = false, updatable = false) // Let DB handle default
    private LocalDateTime loggedTime;
}
