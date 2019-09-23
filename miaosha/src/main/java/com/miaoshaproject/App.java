package com.miaoshaproject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miaoshaproject.dao.UserDoMapper;
import com.miaoshaproject.dataobject.UserDo;

/**
 * Hello world!
 *
 */
@SpringBootApplication(scanBasePackages = {"com.miaoshaproject"})
@RestController
@MapperScan("com.miaoshaproject.dao")
public class App 
{
	@Autowired
    private UserDoMapper userDoMapper;
	
	@RequestMapping("/")
	public String home() {
		UserDo userDo = userDoMapper.selectByPrimaryKey(1);
		if (userDo!=null)
		return userDo.getName();
		else {
			return "Hello World!";
		}
	}
    public static void main( String[] args )
    {
     /*   System.out.println( "Hello World!" );*/
        SpringApplication.run(App.class, args);
    }
}
