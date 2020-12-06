# springboot-learn
springboot学习

**基于当当网的shardingJdbc进行分库分表**
- pom依赖

```
<dependency>
    <groupId>org.apache.shardingsphere</groupId>
    <artifactId>sharding-jdbc-core</artifactId>
    <version>4.0.0</version>
</dependency>
```


- JavaConfig进行数据源配置

```
@Configuration
public class ShardingDataSourceConfig {


    private Map<String, DataSource> createDataSource(){
        // 配置真实数据源
        Map<String, DataSource> dataSourceMap = new HashMap<>();

        // 配置第一个数据源
        DruidDataSource dataSource1 = new DruidDataSource();
        dataSource1.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource1.setUrl("jdbc:mysql://lcyanxi.com:3306/ds0?useUnicode=true&characterEncoding=utf-8");
        dataSource1.setUsername("root");
        dataSource1.setPassword("lc121718");
        dataSourceMap.put("ds0", dataSource1);

        // 配置第二个数据源
        DruidDataSource dataSource2 = new DruidDataSource();
        dataSource2.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource2.setUrl("jdbc:mysql://lcyanxi.com:3306/ds1?useUnicode=true&characterEncoding=utf-8");
        dataSource2.setUsername("root");
        dataSource2.setPassword("lc121718");
        dataSourceMap.put("ds1", dataSource2);
        return dataSourceMap;
    }

    @Bean
    public DataSource getDataSource() throws SQLException {
        //分库设置
        Map<String, DataSource> dataSourceMap = createDataSource();

        // 配置Order表规则
        TableRuleConfiguration tableRuleConfiguration = new TableRuleConfiguration("pr_user_lesson","ds${0..1}.pr_user_lesson_${0..3}");

        // 配置分库 + 分表策略
        tableRuleConfiguration.setDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration("product_id", "ds${product_id % 2}"));
        tableRuleConfiguration.setTableShardingStrategyConfig(new InlineShardingStrategyConfiguration("user_id", "pr_user_lesson_${user_id % 4}"));

        // 配置分片规则
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(tableRuleConfiguration);

        Properties properties = new Properties();
        properties.put("show.sql","true");

        // 获取数据源对象
        return ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, properties);
    }

    @Bean
    public SqlSessionFactory sqlSessionFactoryBean(@Autowired DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/mapper/*.xml"));

        return sqlSessionFactoryBean.getObject();
    }
}
```
- 注意shardingJdbc 1.x版本不支持批量insert


# 大厂常见算法： 

[大厂常见算法](https://github.com/lcyanxi/springboot-learn/blob/master/leetcode%E7%AE%97%E6%B3%95/leetcode%E7%AE%97%E6%B3%95.md)


# 分布式RPC框架： 
- [RPC实战与核心原理-基础篇](https://github.com/lcyanxi/springboot-learn/blob/master/%E5%88%86%E5%B8%83%E5%BC%8FRPC%E6%A1%86%E6%9E%B6/RPC%E5%AE%9E%E6%88%98%E4%B8%8E%E6%A0%B8%E5%BF%83%E5%8E%9F%E7%90%86-%E5%9F%BA%E7%A1%80%E7%AF%87.md)
- [RPC实战与核心原理-进阶篇](https://github.com/lcyanxi/springboot-learn/blob/master/%E5%88%86%E5%B8%83%E5%BC%8FRPC%E6%A1%86%E6%9E%B6/RPC%E5%AE%9E%E6%88%98%E4%B8%8E%E6%A0%B8%E5%BF%83%E5%8E%9F%E7%90%86-%E8%BF%9B%E9%98%B6%E7%AF%87.md)
- [RPC实战与核心原理-高级篇](https://github.com/lcyanxi/springboot-learn/blob/master/%E5%88%86%E5%B8%83%E5%BC%8FRPC%E6%A1%86%E6%9E%B6/RPC%E5%AE%9E%E6%88%98%E4%B8%8E%E6%A0%B8%E5%BF%83%E5%8E%9F%E7%90%86-%E9%AB%98%E7%BA%A7%E7%AF%87.md)






