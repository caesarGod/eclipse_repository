package com.miaoshaproject.controller;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.util.StringUtils;
import com.miaoshaproject.controller.model.UserVo;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.MyService;
import com.miaoshaproject.service.model.UserModel;

import sun.misc.BASE64Encoder;


@Controller("user")
@RequestMapping("/user")

//处理跨域请求
@CrossOrigin(allowCredentials="true", allowedHeaders="*")
public class MyController extends BaseController{

	@Autowired
	private MyService myService;
	
	@Autowired
	private HttpServletRequest httpservletRequest;
	//用户登录接口
	@RequestMapping(value= "/login",method= {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
	@ResponseBody
	public CommonReturnType login(@RequestParam(name="telphone")String telphone,
			               @RequestParam(name="password")String password) throws BusinessException, NoSuchAlgorithmException, UnsupportedEncodingException {
		
		//入参校验
		if (StringUtils.isEmpty(telphone)||StringUtils.isEmpty(password)) {
			throw new BusinessException(EmBusinessError.PARAMENTER_VALIDATION_ERROR);
		}
		
		//用户登录服务，用来验证用户登录是否合法
		UserModel userModel = myService.validateLogin(telphone, this.EncodeByMd5(password));
		
		//将登录凭证加入到用户登录成功的session内
		this.httpservletRequest.getSession().setAttribute("IS_LOGIN", true);
		this.httpservletRequest.getSession().setAttribute("LOGIN_USER", userModel);
		return CommonReturnType.create(null);
	}
	
	
	
	//用户注册接口
	@RequestMapping(value= "/register",method= {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
	@ResponseBody
	public CommonReturnType register(@RequestParam(name="telphone") String telphone,
			               @RequestParam(name="otpCode") String otpCode,
			               @RequestParam(name="name")String name,
			               @RequestParam(name="gender")Byte gender,
			               @RequestParam(name="age")Integer age,
			               @RequestParam(name="password")String password) throws BusinessException, NoSuchAlgorithmException, UnsupportedEncodingException {
			//验证手机号与对应的otpCode是否相符合
         String inSessionOtpCode = (String) this.httpservletRequest.getSession().getAttribute(telphone);
		 if (!com.alibaba.druid.util.StringUtils.equals(otpCode, inSessionOtpCode))
		 {
			 throw new BusinessException(EmBusinessError.PARAMENTER_VALIDATION_ERROR,"短信验证码不正确");
		 }
		 
		 
		 //用户注册流程
		 
		 UserModel userModel = new UserModel();
		 userModel.setName(name);
		 userModel.setAge(age);
		 userModel.setGender(gender);
		 userModel.setTelphone(telphone);
		 userModel.setRegisterMode("byphone");
		 userModel.setEncrptPassword(this.EncodeByMd5(password));
		 
		myService.register(userModel);
		return CommonReturnType.create(null);
		
		
		
	}
	
	public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		//确定计算方法
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		//默认不开放，需要在(configure bulid path)access rule加入一条规则
		BASE64Encoder base64en = new BASE64Encoder();
		//加密字符串
		String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
		return newstr;
	}
	
	//用户获取otp短信验证接口
	@RequestMapping(value= "/getotp",method= {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
	@ResponseBody
	public CommonReturnType getOtp(@RequestParam(name = "telphone") String telphone) {
		//按照一定规则生成otp验证码
		Random random = new Random();
		int randomInt = random.nextInt(99999);
		randomInt += 10000;
		String otpCode = String.valueOf(randomInt);
		//把用户手机号与生成的otp验证码进行绑定,使用httpsession的方式绑定他的手机号与otpCode(还可用radis缓存)
		
		httpservletRequest.getSession().setAttribute(telphone, otpCode);
		
		//将验证码发送到用户手机（无）
		
		System.out.println("telphone ="+ telphone +  "  otpCode = "+ otpCode);
		
		return CommonReturnType.create(null);
	}
	
	@RequestMapping("/get")
	@ResponseBody
	public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BusinessException
	{
		//调用service服务对应id获取用户对象并返回给前端
		UserModel userModel = myService.getUserById(id);
		
		//若获取的对应用户信息不存在
		if (userModel == null) {
			throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
		}
		
		//将核心领域模型转化为可供UI使用的模型viewobject（只提供前端需要的数据）
	   /* return convertUserModel(userModel);*/
		UserVo controlModel = convertUserModel(userModel);
		
		return CommonReturnType.create(controlModel);
	    
	}
	//将userModel层转化为ControlModel层提供UI所需数据
	private UserVo convertUserModel(UserModel userModel) {
		
		UserVo userVo = new UserVo();
		BeanUtils.copyProperties(userModel, userVo);
	    return userVo;
	}
	
}
