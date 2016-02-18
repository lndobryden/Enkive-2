package com.linuxbox.enkive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com/linuxbox/enkive")
public class EnkiveApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(EnkiveApplication.class);
		String OSName = System.getProperty("os.name");
		if (OSName.contains("OS X")) {
			app.setAdditionalProfiles("osx");
		}
		app.run(args);
	}

}
