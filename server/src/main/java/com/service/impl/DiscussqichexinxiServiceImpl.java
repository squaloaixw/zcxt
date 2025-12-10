package com.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper; // 必须导入这个
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.utils.PageUtils;
import com.utils.Query;
import com.dao.DiscussqichexinxiDao;
import com.entity.DiscussqichexinxiEntity;
import com.service.DiscussqichexinxiService;

@Service("discussqichexinxiService")
public class DiscussqichexinxiServiceImpl extends ServiceImpl<DiscussqichexinxiDao, DiscussqichexinxiEntity> implements DiscussqichexinxiService {

    // 原有的方法保持不变
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        EntityWrapper<DiscussqichexinxiEntity> ew = new EntityWrapper<>();
        if(params.get("refid") != null){
            ew.eq("refid", params.get("refid"));
        }
        ew.orderBy("addtime", false);

        Page<DiscussqichexinxiEntity> page = this.selectPage(
                new Query<DiscussqichexinxiEntity>(params).getPage(),
                ew
        );
        return new PageUtils(page);
    }

    // 【新增】实现带有 Wrapper 参数的方法
    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<DiscussqichexinxiEntity> wrapper) {
        Page<DiscussqichexinxiEntity> page = this.selectPage(
                new Query<DiscussqichexinxiEntity>(params).getPage(),
                wrapper
        );
        return new PageUtils(page);
    }
}
