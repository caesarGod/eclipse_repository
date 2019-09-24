package com.miaoshaproject.dao;


import com.miaoshaproject.dataobject.UserPasswordDo;

public interface UserPasswordDoMapper {

	int deleteByPrimaryKey(Integer id);

	int insert(UserPasswordDo record);

	int insertSelective(UserPasswordDo record);

	UserPasswordDo selectByPrimaryKey(Integer id);
//根据用户id查找到相应的密码
	UserPasswordDo selectByUserId(Integer id);
	
	int updateByPrimaryKeySelective(UserPasswordDo record);

	int updateByPrimaryKey(UserPasswordDo record);
}