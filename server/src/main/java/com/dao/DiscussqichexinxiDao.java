package com.dao;

import com.entity.DiscussqichexinxiEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper; // 引入注解

// 确保加上 @Mapper 注解，这样Spring Boot才能扫描到它
@Mapper
public interface DiscussqichexinxiDao extends BaseMapper<DiscussqichexinxiEntity> {

}
