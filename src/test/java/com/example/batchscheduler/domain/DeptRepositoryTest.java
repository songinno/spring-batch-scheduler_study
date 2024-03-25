package com.example.batchscheduler.domain;

import com.example.batchscheduler.domain.dept.Dept;
import com.example.batchscheduler.domain.dept.DeptRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

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