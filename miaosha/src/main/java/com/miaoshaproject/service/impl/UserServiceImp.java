package com.miaoshaproject.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miaoshaproject.dao.UserDoMapper;
import com.miaoshaproject.dao.UserPasswordDoMapper;
import com.miaoshaproject.dataobject.UserDo;
import com.miaoshaproject.dataobject.UserPasswordDo;
import com.miaoshaproject.service.MyService;
import com.miaoshaproject.service.model.UserModel;


@Service
public class UserServiceImp implements MyService{

	@Autowired
	private UserDoMapper userDoMapper;
	
	@Autowired
	private UserPasswordDoMapper userPasswordDoMapper;
	
	@Override
	public UserModel getUserById(Integer id) {
		//通过userdomapper获取用户dataobject	
		UserDo userDo = userDoMapper.selectByPrimaryKey(id);
		
		if (userDo == null) {
			return null;
		}
		//通过用户id获取相应的加密密码
		UserPasswordDo userPasswordDo = userPasswordDoMapper.selectByUserId(id);
		
		
		return convertFromDataObject(userDo, userPasswordDo);
	}
	
	private UserModel convertFromDataObject(UserDo userDo,UserPasswordDo userPasswordDo) {
		if (userDo == null) {
			return null;
		}
		UserModel userModel = new UserModel();
		BeanUtils.copyProperties(userDo, userModel);
		
		if (userPasswordDo != null) {
		userModel.setEncrptPassword(userPasswordDo.getEncrptPassword());
		}
		return userModel;
		
	}

}
