package com.example.batchscheduler.domain.statistics;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class HourlyCountStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    private String relTbl;

    @ColumnDefault("0") // # 테이블 생성 시 초깃값 0으로 세팅
    private int count;

    @CreationTimestamp
    private LocalDateTime createDate;

    public HourlyCountStatistics(String relTbl, int count) {
        this.relTbl = relTbl;
        this.count = count;
    }
}
