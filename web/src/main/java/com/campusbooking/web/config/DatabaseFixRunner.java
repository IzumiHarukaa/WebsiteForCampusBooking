package com.campusbooking.web.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Runs on every startup to apply schema fixes that Hibernate's ddl-auto=update cannot handle.
 *
 * NOTE: A DB-level UNIQUE constraint on (facility_id, date, time_slot) is intentionally NOT used.
 * Reason: MySQL does not support partial/filtered indexes. Rejected bookings stay in the
 * table with their original slot data, so a naive UNIQUE constraint would permanently block
 * that slot even after rejection — preventing re-use of the slot by other students.
 * Conflict detection is enforced at the application layer in BookingService.createBooking(),
 * which correctly excludes REJECTED-status rows from the conflict check.
 */
@Component
public class DatabaseFixRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseFixRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        fixStatusColumn();
        dropBrokenUniqueConstraintIfPresent();
    }

    /** Widen the status column so new _REAPPLIED enum values persist without truncation. */
    private void fixStatusColumn() {
        try {
            jdbcTemplate.execute("ALTER TABLE bookings MODIFY status VARCHAR(255)");
            System.out.println("[Schema] Patched: bookings.status → VARCHAR(255)");
        } catch (Exception e) {
            System.out.println("[Schema] bookings.status already VARCHAR(255) — skipping.");
        }
    }

    /**
     * Drops the naive UNIQUE constraint that was incorrectly applied in a previous version.
     * That constraint blocked slots occupied by REJECTED bookings, which is wrong behaviour.
     */
    private void dropBrokenUniqueConstraintIfPresent() {
        try {
            jdbcTemplate.execute(
                "ALTER TABLE bookings DROP INDEX uq_bookings_facility_date_slot"
            );
            System.out.println("[Schema] Removed broken unique constraint uq_bookings_facility_date_slot.");
        } catch (Exception e) {
            System.out.println("[Schema] Constraint uq_bookings_facility_date_slot not present — nothing to drop.");
        }
    }
}
