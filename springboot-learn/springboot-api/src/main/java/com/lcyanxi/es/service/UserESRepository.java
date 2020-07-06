package com.lcyanxi.es.service;

import com.lcyanxi.es.model.UserES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserESRepository extends ElasticsearchRepository<UserES, Long> {
}
