# springboot-learn
_站在巨人的肩膀上，仰望星空，脚踏实地_

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

# Java并发：
- [深入分析ThreadLocal原理](https://github.com/lcyanxi/springboot-learn/blob/master/Java%E5%B9%B6%E5%8F%91/%E6%B7%B1%E5%85%A5%E5%88%86%E6%9E%90ThreadLocal%E5%8E%9F%E7%90%86.md)
- [ReentrantLock原理分析](https://github.com/lcyanxi/springboot-learn/blob/master/Java%E5%B9%B6%E5%8F%91/ReentrantLock%E5%8E%9F%E7%90%86%E5%88%86%E6%9E%90.md)


# 分布式RPC框架： 
- [RPC实战与核心原理-基础篇](https://github.com/lcyanxi/springboot-learn/blob/master/%E5%88%86%E5%B8%83%E5%BC%8FRPC%E6%A1%86%E6%9E%B6/RPC%E5%AE%9E%E6%88%98%E4%B8%8E%E6%A0%B8%E5%BF%83%E5%8E%9F%E7%90%86-%E5%9F%BA%E7%A1%80%E7%AF%87.md)
- [RPC实战与核心原理-进阶篇](https://github.com/lcyanxi/springboot-learn/blob/master/%E5%88%86%E5%B8%83%E5%BC%8FRPC%E6%A1%86%E6%9E%B6/RPC%E5%AE%9E%E6%88%98%E4%B8%8E%E6%A0%B8%E5%BF%83%E5%8E%9F%E7%90%86-%E8%BF%9B%E9%98%B6%E7%AF%87.md)
- [RPC实战与核心原理-高级篇](https://github.com/lcyanxi/springboot-learn/blob/master/%E5%88%86%E5%B8%83%E5%BC%8FRPC%E6%A1%86%E6%9E%B6/RPC%E5%AE%9E%E6%88%98%E4%B8%8E%E6%A0%B8%E5%BF%83%E5%8E%9F%E7%90%86-%E9%AB%98%E7%BA%A7%E7%AF%87.md)


- [关于RPC dubbo超时原理](https://github.com/lcyanxi/springboot-learn/blob/master/%E5%88%86%E5%B8%83%E5%BC%8FRPC%E6%A1%86%E6%9E%B6/%E5%85%B3%E4%BA%8ERPC%20dubbo%E8%B6%85%E6%97%B6%E5%8E%9F%E7%90%86.md)
- [动态代理在RPC框架中的性能对比](https://github.com/lcyanxi/springboot-learn/blob/master/%E5%88%86%E5%B8%83%E5%BC%8FRPC%E6%A1%86%E6%9E%B6/%E5%8A%A8%E6%80%81%E4%BB%A3%E7%90%86%E5%9C%A8RPC%E6%A1%86%E6%9E%B6%E4%B8%AD%E7%9A%84%E6%80%A7%E8%83%BD%E5%AF%B9%E6%AF%94.md)
- [Dubbo SPI与JDK SPI 实现原理分析](https://github.com/lcyanxi/springboot-learn/blob/master/%E5%88%86%E5%B8%83%E5%BC%8FRPC%E6%A1%86%E6%9E%B6/Dubbo%20SPI%E4%B8%8EJDK%20SPI%20%E5%AE%9E%E7%8E%B0%E5%8E%9F%E7%90%86%E5%88%86%E6%9E%90.md)


# 计算机网络： 
- [输入网址后，期间发生了什么？](https://github.com/lcyanxi/springboot-learn/blob/master/%E8%AE%A1%E7%AE%97%E6%9C%BA%E7%BD%91%E7%BB%9C/%E8%BE%93%E5%85%A5%E7%BD%91%E5%9D%80%E5%90%8E%EF%BC%8C%E6%9C%9F%E9%97%B4%E5%8F%91%E7%94%9F%E4%BA%86%E4%BB%80%E4%B9%88.mdv)
- [TCP 重传、滑动窗口、流量控制、拥塞控制机制？](https://github.com/lcyanxi/springboot-learn/blob/master/%E8%AE%A1%E7%AE%97%E6%9C%BA%E7%BD%91%E7%BB%9C/TCP%20%E9%87%8D%E4%BC%A0%E3%80%81%E6%BB%91%E5%8A%A8%E7%AA%97%E5%8F%A3%E3%80%81%E6%B5%81%E9%87%8F%E6%8E%A7%E5%88%B6%E3%80%81%E6%8B%A5%E5%A1%9E%E6%8E%A7%E5%88%B6%E6%9C%BA%E5%88%B6.md)

# 数据库原理： 




