package com.controller;

import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.entity.DiscussqichexinxiEntity;
import com.service.DiscussqichexinxiService;
import com.service.YonghuService;
import com.entity.YonghuEntity;
import com.utils.R;
import com.utils.PageUtils;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import java.util.List;

@RestController
@RequestMapping("/discussqichexinxi")
public class DiscussqichexinxiController {
    @Autowired
    private DiscussqichexinxiService discussqichexinxiService;

    @Autowired
    private YonghuService yonghuService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        EntityWrapper<DiscussqichexinxiEntity> ew = new EntityWrapper<DiscussqichexinxiEntity>();

        if(params.get("refid") != null){
            ew.eq("refid", params.get("refid"));
        }

        ew.eq("parentid", 0);

        PageUtils page = discussqichexinxiService.queryPage(params, ew);

        // 5. 获取查询到的主评论列表
        List<DiscussqichexinxiEntity> list = (List<DiscussqichexinxiEntity>)page.getList();

        // 6. 遍历主评论，查找子评论
        if(list != null && list.size() > 0){
            for(DiscussqichexinxiEntity parent : list){
                // 查询该主评论下的所有回复
                EntityWrapper<DiscussqichexinxiEntity> childEw = new EntityWrapper<DiscussqichexinxiEntity>();
                childEw.eq("parentid", parent.getId());
                childEw.orderDesc(java.util.Collections.singleton("addtime")); // 子评论按时间倒序

                List<DiscussqichexinxiEntity> children = discussqichexinxiService.selectList(childEw);
                parent.setReplys(children);
            }
        }

        // 7. 将处理好的列表放回 page 对象
        page.setList(list);

        return R.ok().put("data", page);
    }

    /**
     * 新增 (评论/回复)
     */
    @PostMapping("/add")
    public R add(@RequestBody DiscussqichexinxiEntity discussqichexinxi, HttpServletRequest request){
        discussqichexinxi.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());

        // 自动补全用户信息，防止前端伪造或数据缺失
        Long userId = discussqichexinxi.getUserid();
        if(userId != null) {
            YonghuEntity user = yonghuService.selectById(userId);
            if(user != null) {
                discussqichexinxi.setNickname(user.getXingming()); // 假设用户表姓名字段是 xingming
                discussqichexinxi.setAvatarurl(user.getTouxiang()); // 假设头像字段是 touxiang
            }
        }

        discussqichexinxi.setAddtime(new Date());
        discussqichexinxiService.insert(discussqichexinxi);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        discussqichexinxiService.deleteBatchIds(java.util.Arrays.asList(ids));
        return R.ok();
    }
}
