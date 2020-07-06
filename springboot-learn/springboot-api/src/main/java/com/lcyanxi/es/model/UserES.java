package com.lcyanxi.es.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author lichang
 * @date 2020/6/23
 */
@Document(indexName = "user", type = "docs", shards = 1, replicas = 0)
@Data
public class UserES {

    //主键自增长
    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String userName;
    private String userPhone;

}
