package com.miaoshaproject.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miaoshaproject.dao.ItemDoMapper;
import com.miaoshaproject.dao.ItemStockDoMapper;
import com.miaoshaproject.dataobject.ItemDo;
import com.miaoshaproject.dataobject.ItemStockDo;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.model.ItemModel;
import com.miaoshaproject.validator.ValidationResult;
import com.miaoshaproject.validator.ValidatorImpl;
@Service
public class ItemServiceImp implements ItemService{

	@Autowired
	private ValidatorImpl validator;
	
	@Autowired
	private ItemDoMapper itemDoMapper;
	
	@Autowired
	private ItemStockDoMapper itemStockDoMapper;
	
	//itemModel-->ItemDo
	private ItemDo convertFromItemModel(ItemModel itemModel) {
		if (itemModel == null) {
			return null;
		}
		ItemDo itemDo = new ItemDo();
		BeanUtils.copyProperties(itemModel, itemDo);
		itemDo.setPrice(itemModel.getPrice().doubleValue());
		return itemDo;
		
	}
	//itemModel-->ItemStockDo
	private ItemStockDo convertStockFromModel(ItemModel itemModel) {
		if (itemModel == null) {
			return null;
		}
		ItemStockDo itemStockDo = new ItemStockDo();
		
		itemStockDo.setStock(itemModel.getStock());
		itemStockDo.setItemId(itemModel.getId());
		
		return itemStockDo;
		
	}

	@Override
	@Transactional
	public ItemModel createItem(ItemModel itemModel) throws BusinessException {
	//校验入参
    ValidationResult validationResult = validator.validate(itemModel);
	   if (validationResult.isHasError()) {
		throw new BusinessException(EmBusinessError.PARAMENTER_VALIDATION_ERROR,validationResult.getErrMsg());
	}
		
    //转化itemmodel ->dataobject
		ItemDo itemDo = this.convertFromItemModel(itemModel);
		
	//写入数据库
		itemDoMapper.insertSelective(itemDo);
		//获取自增主键
		itemModel.setId(itemDo.getId());
		
		ItemStockDo itemStockDo = convertStockFromModel(itemModel);
	    itemStockDoMapper.insertSelective(itemStockDo);
		
    //返回创建完成的对象
		return this.getItemById(itemModel.getId());
	}

	@Override
	public List<ItemModel> listItem() {
        List<ItemDo> itemDoList = itemDoMapper.listItem();
        List<ItemModel> itemModelList =  itemDoList.stream().map(itemDo->{
        	ItemStockDo itemStockDo = itemStockDoMapper.selectByItemId(itemDo.getId());
        	ItemModel itemModel = this.convertFromDataObject(itemDo, itemStockDo);
        	return itemModel;
        }).collect(Collectors.toList());

		return itemModelList;
	}

	@Override
	public ItemModel getItemById(Integer id) {
	
		ItemDo itemDo = itemDoMapper.selectByPrimaryKey(id);
		if (itemDo == null) {
			return null;
		}
		//获取库存量
		ItemStockDo itemStockDo = itemStockDoMapper.selectByItemId(itemDo.getId());
		
		//dataObject -->itemModel
		ItemModel itemModel = convertFromDataObject(itemDo, itemStockDo);
        
		return itemModel;
	}
	//DataObject(ItemDo、ItemStockDo)---->ItemModel
	private ItemModel convertFromDataObject(ItemDo itemDo,ItemStockDo itemStockDo) {
		
		if (itemDo == null) {
			return null;
		}
		ItemModel itemModel = new ItemModel();
		BeanUtils.copyProperties(itemDo, itemModel);
		//double --->BigDecimal
		itemModel.setPrice(new BigDecimal(itemDo.getPrice()));
		itemModel.setStock(itemStockDo.getStock());;
		
		return itemModel; 
	}
	@Override
	@Transactional
	public boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException {
	    ItemStockDo itemStockDo = 	itemStockDoMapper.selectByPrimaryKey(itemId);
		int affectedRow = itemStockDoMapper.decreaseStock(itemId, amount);
		if (affectedRow > 0) {
			//更新成功
			return true;
		}
		else {
			//更新失败
			return false;
		}
	
	}
	@Override
	@Transactional
	public void increaseSales(Integer itemId, Integer amount) throws BusinessException {
		
		itemDoMapper.increaseSales(itemId, amount);
	}

}
