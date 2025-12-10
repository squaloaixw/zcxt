package com.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.utils.PageUtils;
import com.utils.Query;
import com.dao.DiscussqichexinxiDao;
import com.entity.DiscussqichexinxiEntity;
import com.service.DiscussqichexinxiService;

@Service("discussqichexinxiService")
public class DiscussqichexinxiServiceImpl extends ServiceImpl<DiscussqichexinxiDao, DiscussqichexinxiEntity> implements DiscussqichexinxiService {
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        // 这里可以根据 parentid 排序或者筛选
        EntityWrapper<DiscussqichexinxiEntity> ew = new EntityWrapper<>();
        // 比如只查某个车的评论
        if(params.get("refid") != null){
            ew.eq("refid", params.get("refid"));
        }
        ew.orderBy("addtime", false); // 按时间倒序

        Page<DiscussqichexinxiEntity> page = this.selectPage(
                new Query<DiscussqichexinxiEntity>(params).getPage(),
                ew
        );
        return new PageUtils(page);
    }
}
