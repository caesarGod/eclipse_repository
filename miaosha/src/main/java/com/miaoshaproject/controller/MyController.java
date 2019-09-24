package com.miaoshaproject.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.miaoshaproject.controller.model.ControlModel;
import com.miaoshaproject.service.MyService;
import com.miaoshaproject.service.model.UserModel;

@Controller("user")
@RequestMapping("/user")
public class MyController {

	@Autowired
	private MyService myService;
	
	
	@RequestMapping("/get")
	@ResponseBody
	public ControlModel getUser(@RequestParam(name = "id") Integer id)
	{
		//调用service服务对应id获取用户对象并返回给前端
		UserModel userModel = myService.getUserById(id);
		
		//将核心领域模型转化为可供UI使用的模型（只提供前端需要的数据）
	    return convertUserModel(userModel);
	    
	}
	private ControlModel convertUserModel(UserModel userModel) {
		
		ControlModel controlModel = new ControlModel();
		BeanUtils.copyProperties(userModel, controlModel);
	    return controlModel;
	}
}
