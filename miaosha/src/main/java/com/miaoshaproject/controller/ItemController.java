package com.miaoshaproject.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.miaoshaproject.controller.model.ItemVo;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.model.ItemModel;

@Controller("/item")
@RequestMapping("/item")
@CrossOrigin(allowCredentials="true", allowedHeaders="*")
public class ItemController extends BaseController{

	@Autowired
	private ItemService itemService;
	//创建商品的controller
	@RequestMapping(value= "/create",method= {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
	@ResponseBody
	public CommonReturnType createItem(@RequestParam(name="title")String title,
			              @RequestParam(name="description")String description,
			              @RequestParam(name="price")BigDecimal price,
			              @RequestParam(name="stock")Integer stock,
			              @RequestParam(name="imgUrl")String imgUrl) throws BusinessException {
							
		
		//封装service请求用来创建商品
		ItemModel itemModel = new ItemModel();
		itemModel.setTitle(title);
		itemModel.setDescription(description);
		itemModel.setImgUrl(imgUrl);
		itemModel.setStock(stock);
		itemModel.setPrice(price);
		
		ItemModel itemModelForReturn = itemService.createItem(itemModel);
		ItemVo itemVo = convertVoFromModel(itemModelForReturn);
		return CommonReturnType.create(itemVo);
		
	}
	
	//商品详情页浏览(采用get请求方式，对服务端不发生任何变化的操作)
	@RequestMapping(value= "/get",method= {RequestMethod.GET})
	@ResponseBody
	public CommonReturnType getItem(@RequestParam(name="id")Integer id) {
		ItemModel itemModel = itemService.getItemById(id);
		
		ItemVo itemVo = convertVoFromModel(itemModel);
		
		return CommonReturnType.create(itemVo);

	}
	private ItemVo convertVoFromModel(ItemModel itemModel) {
		
		if (itemModel == null) {
			return null;
		}
		ItemVo itemVo = new ItemVo();
        BeanUtils.copyProperties(itemModel, itemVo);		
		return itemVo;
		
	}
	
	//商品列表浏览
		@RequestMapping(value= "/list",method= {RequestMethod.GET})
		@ResponseBody
		public CommonReturnType listItem() {
			List<ItemModel> itemModelList = itemService.listItem();
			
			//使用stream api 将 list 中的itemmodel 转化未  itemVo
			List<ItemVo> itemVoList = itemModelList.stream().map(itemModel->{
				ItemVo itemVo = this.convertVoFromModel(itemModel);
				return itemVo;
			}).collect(Collectors.toList());
			return CommonReturnType.create(itemVoList);
			
		}
}
