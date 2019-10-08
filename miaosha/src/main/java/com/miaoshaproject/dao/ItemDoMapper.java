package com.miaoshaproject.dao;

import java.util.List;

import com.miaoshaproject.dataobject.ItemDo;

public interface ItemDoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ItemDo record);

    int insertSelective(ItemDo record);

    List<ItemDo> listItem();
    
    ItemDo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ItemDo record);

    int updateByPrimaryKey(ItemDo record);
}