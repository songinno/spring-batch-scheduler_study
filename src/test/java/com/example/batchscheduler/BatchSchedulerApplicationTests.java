package com.example.batchscheduler;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
class BatchSchedulerApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void makeDateFormatTest() {
		Date now = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String newFormat = simpleDateFormat.format(now);
		System.out.println("newFormat = " + newFormat);

	}

}
