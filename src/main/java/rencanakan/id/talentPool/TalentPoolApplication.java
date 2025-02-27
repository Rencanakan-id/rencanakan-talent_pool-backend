package rencanakan.id.talentPool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages={
		"com.example.something", "com.example.application"})
public class TalentPoolApplication {

	public static void main(String[] args) {
		SpringApplication.run(TalentPoolApplication.class, args);
	}

}
