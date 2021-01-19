package com.lcyanxi.springbootshardingsphere;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lcyanxi.model.Course;
import java.util.List;
import javax.annotation.Resource;
import mapper.CourseMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author lichang
 * @date 2021/1/19
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ShardingJDBCTest {
    @Resource
    CourseMapper courseMapper;

    @Test
    public void addCourse(){
        for(int i = 0 ; i < 10 ; i ++){
            Course c = new Course();
//            c.setCid(Long.valueOf(i));
            c.setCname("shardingsphere");
            c.setUserId(Long.valueOf(""+(1000+i)));
            c.setCstatus("1");
            courseMapper.insert(c);
        }
    }

    @Test
    public void queryCourse(){
        //select * from course where cid = ''
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("cid");
        wrapper.eq("cid",558419671351562240L);
//        wrapper.in()
        List<Course> courses = courseMapper.selectList(wrapper);
        courses.forEach(System.out::println);
    }

    @Test
    public void queryOrderRange(){
        //select * from course where cid in ()
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.in("cid",558419671984902145L,558419673041866753L);
        List<Course> courses = courseMapper.selectList(wrapper);
        courses.forEach(course -> System.out.println(course));
    }


    @Test
    public void queryOrderBetween(){
        //select * from course where cid between '' and  '' order by cid desc
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.between("cid",558419672463052800L,558419673041866753L);
        wrapper.orderByDesc("user_id");
        List<Course> courses = courseMapper.selectList(wrapper);
        courses.forEach(System.out::println);
    }

    @Test
    public void queryCourseComplex(){
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.between("cid",558419671984902145L,558419673041866753L);
        wrapper.eq("user_id",1001);
        List<Course> courses = courseMapper.selectList(wrapper);
        courses.forEach(System.out::println);
    }
//
//    @Test
//    public void queryCourseByHint(){
//        HintManager hintManager = HintManager.getInstance();
//        hintManager.addTableShardingValue("course",2);
//        List<Course> courses = courseMapper.selectList(null);
//        courses.forEach(course -> System.out.println(course));
//        hintManager.close();
//    }
//
//    @Test
//    public void addDict(){
//        Dict d1 = new Dict();
//        d1.setUstatus("1");
//        d1.setUvalue("正常");
//        dictMapper.insert(d1);
//
//        Dict d2 = new Dict();
//        d2.setUstatus("0");
//        d2.setUvalue("不正常");
//        dictMapper.insert(d2);
//
//        for(int i = 0 ; i < 10 ; i ++){
//            User user = new User();
//            user.setUsername("user No "+i);
//            user.setUstatus(""+(i%2));
//            user.setUage(i*10);
//            userMapper.insert(user);
//        }
//    }
//
//    @Test
//    public void queryUserStatus(){
//        List<User> users = userMapper.queryUserStatus();
//        users.forEach(user -> System.out.println(user));
//    }
//
//    @Test
//    public void addDictByMS(){
//        Dict d1 = new Dict();
//        d1.setUstatus("1");
//        d1.setUvalue("正常");
//        dictMapper.insert(d1);
//
//        Dict d2 = new Dict();
//        d2.setUstatus("0");
//        d2.setUvalue("不正常");
//        dictMapper.insert(d2);
//    }
//
//    @Test
//    public void queryDictByMS(){
//        List<Dict> dicts = dictMapper.selectList(null);
//        dicts.forEach(dict -> System.out.println(dict));
//    }
}
