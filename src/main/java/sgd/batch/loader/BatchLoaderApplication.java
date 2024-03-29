package sgd.batch.loader;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class BatchLoaderApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchLoaderApplication.class, args);
	}

}
