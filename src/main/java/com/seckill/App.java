package com.seckill;

import com.seckill.dao.UserDOMapper;
import com.seckill.dos.UserDO;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@SpringBootApplication(scanBasePackages = {"com.seckill"}) //自动扫描seckill包下面带注解的类
@MapperScan("com.seckill.dao") //设置dao接口的位置
//@EnableAutoConfiguration 这个注解和SpringBootApplication功能几乎一样，都使用springboot自动配置托管这个类的启动
public class App {
    @Autowired
    private UserDOMapper userDOMapper;
    @RequestMapping("/")
    public String home(){
        UserDO userDO = userDOMapper.selectByPrimaryKey(1);
        if(userDO == null){
            return "用户不存在";
        }else{
            return userDO.getName();
        }
    }
    public static void main( String[] args ) {
        SpringApplication.run(App.class, args);
    }
}
