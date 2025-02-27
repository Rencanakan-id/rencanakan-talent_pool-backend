package rencanakan.id.talentPool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class TalentPoolApplication {

	public static void main(String[] args) {
		SpringApplication.run(TalentPoolApplication.class, args);
	}

}
