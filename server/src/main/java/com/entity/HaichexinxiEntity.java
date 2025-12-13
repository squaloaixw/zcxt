package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;

@TableName("haichexinxi")
public class HaichexinxiEntity<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	public HaichexinxiEntity() {}

	public HaichexinxiEntity(T t) {
		try {
			BeanUtils.copyProperties(this, t);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@TableId
	private Long id;
	private String dingdanbianhao;
	private String chepaihao;
	private String cheliangpinpai;
	private String qicheleibie;

	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
	@DateTimeFormat
	private Date haicheriqi;

	private String haichebeizhu;
	private String zhanghao;
	private String xingming;
	private String shouji;
	private String shenfenzheng;
	private String guanlizhanghao;
	private String guanlixingming;
	private Long crossuserid;
	private Long crossrefid;
	private String sfsh;
	private String shhf;

	// --- 新增字段 ---
	private String jiesuanleixing; // 结算类型
	private Float tuihuanjine;     // 退还金额
	private Float bujiaojine;      // 补缴金额
	// ----------------

	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	private Date addtime;

	// Getter 和 Setter 方法
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getDingdanbianhao() { return dingdanbianhao; }
	public void setDingdanbianhao(String dingdanbianhao) { this.dingdanbianhao = dingdanbianhao; }

	public String getChepaihao() { return chepaihao; }
	public void setChepaihao(String chepaihao) { this.chepaihao = chepaihao; }

	public String getCheliangpinpai() { return cheliangpinpai; }
	public void setCheliangpinpai(String cheliangpinpai) { this.cheliangpinpai = cheliangpinpai; }

	public String getQicheleibie() { return qicheleibie; }
	public void setQicheleibie(String qicheleibie) { this.qicheleibie = qicheleibie; }

	public Date getHaicheriqi() { return haicheriqi; }
	public void setHaicheriqi(Date haicheriqi) { this.haicheriqi = haicheriqi; }

	public String getHaichebeizhu() { return haichebeizhu; }
	public void setHaichebeizhu(String haichebeizhu) { this.haichebeizhu = haichebeizhu; }

	public String getZhanghao() { return zhanghao; }
	public void setZhanghao(String zhanghao) { this.zhanghao = zhanghao; }

	public String getXingming() { return xingming; }
	public void setXingming(String xingming) { this.xingming = xingming; }

	public String getShouji() { return shouji; }
	public void setShouji(String shouji) { this.shouji = shouji; }

	public String getShenfenzheng() { return shenfenzheng; }
	public void setShenfenzheng(String shenfenzheng) { this.shenfenzheng = shenfenzheng; }

	public String getGuanlizhanghao() { return guanlizhanghao; }
	public void setGuanlizhanghao(String guanlizhanghao) { this.guanlizhanghao = guanlizhanghao; }

	public String getGuanlixingming() { return guanlixingming; }
	public void setGuanlixingming(String guanlixingming) { this.guanlixingming = guanlixingming; }

	public Long getCrossuserid() { return crossuserid; }
	public void setCrossuserid(Long crossuserid) { this.crossuserid = crossuserid; }

	public Long getCrossrefid() { return crossrefid; }
	public void setCrossrefid(Long crossrefid) { this.crossrefid = crossrefid; }

	public String getSfsh() { return sfsh; }
	public void setSfsh(String sfsh) { this.sfsh = sfsh; }

	public String getShhf() { return shhf; }
	public void setShhf(String shhf) { this.shhf = shhf; }

	public Date getAddtime() { return addtime; }
	public void setAddtime(Date addtime) { this.addtime = addtime; }

	// --- 新增字段的 Getter/Setter ---
	public String getJiesuanleixing() { return jiesuanleixing; }
	public void setJiesuanleixing(String jiesuanleixing) { this.jiesuanleixing = jiesuanleixing; }

	public Float getTuihuanjine() { return tuihuanjine; }
	public void setTuihuanjine(Float tuihuanjine) { this.tuihuanjine = tuihuanjine; }

	public Float getBujiaojine() { return bujiaojine; }
	public void setBujiaojine(Float bujiaojine) { this.bujiaojine = bujiaojine; }
}
