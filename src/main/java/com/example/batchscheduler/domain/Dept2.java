package com.example.batchscheduler.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Dept2 {

    @Id
    Integer deptNo;
    String dName;
    String loc;
}
