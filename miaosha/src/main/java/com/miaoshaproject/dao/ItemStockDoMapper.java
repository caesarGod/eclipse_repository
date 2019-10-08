package com.miaoshaproject.dao;

import com.miaoshaproject.dataobject.ItemStockDo;

public interface ItemStockDoMapper {

	int deleteByPrimaryKey(Integer id);

    int insert(ItemStockDo record);

    int insertSelective(ItemStockDo record);

    ItemStockDo selectByPrimaryKey(Integer id);
    
    ItemStockDo selectByItemId(Integer itemId);
    
    int updateByPrimaryKeySelective(ItemStockDo record);

    int updateByPrimaryKey(ItemStockDo record);
}