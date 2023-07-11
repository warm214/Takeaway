package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    ShoppingCartMapper shoppingCartMapper;
    @Autowired
    DishMapper dishMapper;
    @Autowired
    SetmealMapper setmealMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //判断当前加入到购物车中的商品是否已经存在
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        List<ShoppingCart> list=shoppingCartMapper.list(shoppingCart);
        //存在，数量加一
        if (list!=null &&list.size()>0){
            ShoppingCart shoppingCart1 = list.get(0);
            shoppingCart1.setNumber(shoppingCart1.getNumber()+1);
            shoppingCartMapper.updateNumberById(shoppingCart1);
        }else {
            //不存在，插入数据
            Long dishId = shoppingCartDTO.getDishId();
            //判断添加到购物车的是菜品还是套餐
            if(dishId!=null){
                //本次添加到购物车的是菜品
                Dish byId = dishMapper.getById(dishId);
                String name = byId.getName();
                String image = byId.getImage();
                BigDecimal price = byId.getPrice();
                shoppingCart.setAmount(price);
                shoppingCart.setImage(image);
                shoppingCart.setName(name);
            }else {
                //本次添加到购物车的是套餐
                Setmeal byId = setmealMapper.getById(shoppingCartDTO.getSetmealId());
                shoppingCart.setAmount(byId.getPrice());
                shoppingCart.setImage(byId.getImage());
                shoppingCart.setName(byId.getName());

            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }


    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        Long currentId = BaseContext.getCurrentId();
        ShoppingCart build = ShoppingCart.builder().userId(currentId).build();
        return shoppingCartMapper.list(build);
    }


    @Override
    public void cleanShoppingCart() {
        Long currentId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(currentId);
    }
}
