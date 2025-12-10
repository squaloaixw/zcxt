package com.controller;

import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.entity.DiscussqichexinxiEntity;
import com.service.DiscussqichexinxiService;
import com.service.YonghuService; // 引入用户Service以获取最新头像昵称
import com.entity.YonghuEntity;
import com.utils.R;
import com.utils.PageUtils;

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
        PageUtils page = discussqichexinxiService.queryPage(params);
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
