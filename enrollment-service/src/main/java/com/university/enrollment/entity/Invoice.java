package com.university.enrollment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * EAGER – toDto() always calls i.getEnrollment().getId().
     * Without EAGER (or Hibernate bytecode enhancement), accessing a LAZY
     * @OneToOne on the owning side outside an active session proxy throws
     * LazyInitializationException even when @Transactional is present,
     * because Spring AOP proxies only intercept public method boundaries.
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "enrollment_id", unique = true)
    private Enrollment enrollment;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String status = "PENDING";

    private LocalDate dueDate;
    private String description;
    private String currency = "USD";

    /*
     * EAGER – toDto() always streams i.getPayments().
     * @OneToMany is LAZY by default; without EAGER the collection is an
     * uninitialised PersistentBag proxy that throws LazyInitializationException
     * as soon as it is accessed after the Hibernate session closes.
     */
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Payment> payments = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() { updatedAt = LocalDateTime.now(); }
}
