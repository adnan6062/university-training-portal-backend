package com.university.enrollment.repository;

import com.university.enrollment.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    /**
     * Fetches a single Invoice by its own PK, eagerly loading both
     * the enrollment (OneToOne) and payments (OneToMany) collections
     * so that toDto() never triggers a LazyInitializationException.
     */
    @Query("""
        SELECT DISTINCT i FROM Invoice i
        LEFT JOIN FETCH i.enrollment
        LEFT JOIN FETCH i.payments
        WHERE i.id = :id
    """)
    Optional<Invoice> findByIdWithDetails(@Param("id") Long id);

    /**
     * Fetches a single Invoice by enrollment PK, eagerly loading associations.
     * @Param is required so Spring Data can bind :enrollmentId regardless of
     * whether the -parameters compiler flag produced MethodParameters entries.
     */
    @Query("""
        SELECT DISTINCT i FROM Invoice i
        LEFT JOIN FETCH i.enrollment
        LEFT JOIN FETCH i.payments
        WHERE i.enrollment.id = :enrollmentId
    """)
    Optional<Invoice> findByEnrollmentIdWithDetails(@Param("enrollmentId") Long enrollmentId);

    /**
     * Fetches all Invoices with associations eagerly loaded.
     * DISTINCT avoids duplicate Invoice rows caused by the payments JOIN.
     */
    @Query("""
        SELECT DISTINCT i FROM Invoice i
        LEFT JOIN FETCH i.enrollment
        LEFT JOIN FETCH i.payments
    """)
    List<Invoice> findAllWithDetails();

    List<Invoice> findByStatus(String status);
}
