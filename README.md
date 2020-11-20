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


- **lettcode**

#####  1.反转字符串
**描述**：
编写一个函数，其作用是将输入的字符串反转过来。输入字符串以字符数组 char[] 的形式给出。

不要给另外的数组分配额外的空间，你必须原地修改输入数组、使用 O(1) 的额外空间解决这一问题。

你可以假设数组中的所有字符都是 ASCII码表中的可打印字符。

**示例 1：**

```
输入：["h","e","l","l","o"]
输出：["o","l","l","e","h"]
```

##### 2.删除链表的节点
**描述**：给定单向链表的头指针和一个要删除的节点的值，定义一个函数删除该节点。

返回删除后的链表的头节点。
```
输入: head = [4,5,1,9], val = 5
输出: [4,1,9]
```
##### 3.数组中重复的数字
**描述**：在一个长度为 n 的数组 nums 里的所有数字都在 0～n-1 的范围内。数组中某些数字是重复的，但不知道有几个数字重复了，也不知道每个数字重复了几次。请找出数组中任意一个重复的数字。

```
输入：[2, 3, 1, 0, 2, 5, 3]
输出：2 或 3 
```
##### 4.合并两个排序的链表
**描述**：输入两个递增排序的链表，合并这两个链表并使新链表中的节点仍然是递增排序的。

```
输入：1->2->4, 1->3->4
输出：1->1->2->3->4->4
```




