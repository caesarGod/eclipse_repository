package com.miaoshaproject.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.util.StringUtils;
import com.miaoshaproject.controller.model.UserVo;
import com.miaoshaproject.dao.UserDoMapper;
import com.miaoshaproject.dao.UserPasswordDoMapper;
import com.miaoshaproject.dataobject.UserDo;
import com.miaoshaproject.dataobject.UserPasswordDo;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.service.MyService;
import com.miaoshaproject.service.model.UserModel;
import com.miaoshaproject.validator.ValidationResult;
import com.miaoshaproject.validator.ValidatorImpl;


@Service
public class UserServiceImp implements MyService {

	@Autowired
	private UserDoMapper userDoMapper;
	
	@Autowired
	private UserPasswordDoMapper userPasswordDoMapper;
	
	@Autowired
	private ValidatorImpl validator;
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

	@Override
	//防止userDoMapper，userPasswordDoMapper的插入操作不在同一事务处境
	@Transactional
	public void register(UserModel userModel) throws BusinessException {
		// TODO Auto-generated method stub
		if (userModel == null) {
			throw new BusinessException(EmBusinessError.PARAMENTER_VALIDATION_ERROR);
		}
		
/*		if (StringUtils.isEmpty(userModel.getName())
				|| userModel.getGender()==null
	            || userModel.getAge() ==null
	            || StringUtils.isEmpty(userModel.getTelphone())) {
			throw new BusinessException(EmBusinessError.PARAMENTER_VALIDATION_ERROR);
			
		}*/
	
		ValidationResult result = validator.validate(userModel);
		if (result.isHasError()) {
			throw new BusinessException(EmBusinessError.PARAMENTER_VALIDATION_ERROR,result.getErrMsg());
		}
		
		//UserModel-->ControlModel
		UserDo userDo = convertFromUserModel(userModel);
		try {
			userDoMapper.insertSelective(userDo);
		} catch (DuplicateKeyException e) {
			throw new BusinessException(EmBusinessError.PARAMENTER_VALIDATION_ERROR,"手机号已注册");
		}
		
		
		userModel.setId(userDo.getId());
		
		UserPasswordDo userPasswordDo = convertPasswordFromUserModel(userModel);
		userPasswordDoMapper.insertSelective(userPasswordDo);
		
		return;
	}
	
	//将UserModel-->userPasswordDo
	private UserPasswordDo convertPasswordFromUserModel(UserModel userModel) {
		if (userModel == null) {
			return null;
		}
		UserPasswordDo userPasswordDo = new UserPasswordDo();
		userPasswordDo.setEncrptPassword(userModel.getEncrptPassword());
		userPasswordDo.setUserId(userModel.getId());
		return userPasswordDo;
		
	}
	//将UserModel-->UserDo
	private UserDo convertFromUserModel(UserModel userModel) {
		if (userModel == null) {
			return null;
		}
		UserDo userDo = new UserDo();
		BeanUtils.copyProperties(userModel, userDo);
		
		return userDo;
		
	}

	@Override
	public UserModel validateLogin(String telphone, String encrptPassword) throws BusinessException {
		// TODO Auto-generated method stub
		//通过用户手机获取用户信息
		UserDo userDo = userDoMapper.selectByTelphone(telphone);
	    if(userDo == null) {
	    	throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
	    }
	    
		UserPasswordDo userPasswordDo = userPasswordDoMapper.selectByUserId(userDo.getId());
 	    
		UserModel userModel = convertFromDataObject(userDo, userPasswordDo);
		
		//对比用户信息内加密的密码与传输进来的密码是否相同
		if(!StringUtils.equals(encrptPassword, userModel.getEncrptPassword())) {
			throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
		}
		return userModel;
		
	}

}
