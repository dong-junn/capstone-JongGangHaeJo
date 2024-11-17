package jeiu.capstone.jongGangHaejo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class JongGangHaejoApplication {

	public static void main(String[] args) {
		log.info(System.getenv("DB_PORT"));
		log.info(System.getenv("DB_USERNAME"));
		log.info(System.getenv("S3_BUCKET_NAME"));
		log.info(System.getenv("MAIL_USERNAME"));
		SpringApplication.run(JongGangHaejoApplication.class, args);
	}

}
