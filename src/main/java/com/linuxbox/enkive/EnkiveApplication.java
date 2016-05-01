package com.linuxbox.enkive;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EnkiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnkiveApplication.class, args);
	}

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Bean
	public Queue enkiveQueue() {
		//Create a Queue named enkive, mark it durable
		return new Queue("enkive", true);
	}

}
