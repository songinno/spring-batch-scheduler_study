package com.example.batchscheduler.domain.statistics;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HourlyCountStatisticsRepository extends JpaRepository<HourlyCountStatistics, Long> {
}
