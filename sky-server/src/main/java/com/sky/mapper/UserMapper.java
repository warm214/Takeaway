package com.sky.mapper;

import com.sky.entity.Setmeal;
import com.sky.entity.User;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface UserMapper {
    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("select *from user where openid=openid")
    User getByOpenid(String openid);

    /**
     *
     * @param user
     */
    void insert(User user);

    /**
     * 根据id查询用户
     * @param userId
     * @return
     */
    @Select("select *from user where id=#{userId}")
    User getById(Long userId);
}





















