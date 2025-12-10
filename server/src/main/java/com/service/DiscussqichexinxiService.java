package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper; // 必须导入这个
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.DiscussqichexinxiEntity;
import java.util.Map;

public interface DiscussqichexinxiService extends IService<DiscussqichexinxiEntity> {
    PageUtils queryPage(Map<String, Object> params);
    PageUtils queryPage(Map<String, Object> params, Wrapper<DiscussqichexinxiEntity> wrapper);
}
