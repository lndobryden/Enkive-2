package com.linuxbox.enkive.repository;

import com.linuxbox.enkive.message.EnkiveMessage;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

/**
 *
 */
public interface EnkiveMessageRepository extends ElasticsearchCrudRepository<EnkiveMessage, String> {

}
