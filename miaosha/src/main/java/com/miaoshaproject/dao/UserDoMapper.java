package com.miaoshaproject.dao;


import com.miaoshaproject.dataobject.UserDo;

public interface UserDoMapper {

	int deleteByPrimaryKey(Integer id);

	int insert(UserDo record);

	int insertSelective(UserDo record);

	UserDo selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(UserDo record);

	int updateByPrimaryKey(UserDo record);
}