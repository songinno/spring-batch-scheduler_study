package com.example.batchscheduler.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DeptRepositoryTest {
    @Autowired
    DeptRepository deptRepository;

    @Test
    @Commit
    public void dept01() {
        for (int i = 0; i < 100; i++) {
            deptRepository.save(new Dept(i, "dname_" + String.valueOf(i), "loc_" + String.valueOf(i)));
        }
    }
}