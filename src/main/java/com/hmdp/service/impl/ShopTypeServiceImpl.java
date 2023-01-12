package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryTypeList() {
        // 从redis中查询商户
        String shopType = stringRedisTemplate.opsForValue().get("shopType");
        if (StrUtil.isNotBlank(shopType)) {
            // 存在，直接返回
            List<ShopType> shopTypes = JSONUtil.toList(shopType, ShopType.class);
            return Result.ok(shopTypes);
        }
        // 不存在，从数据库中查询
        List<ShopType> shopTypes = query().orderByAsc("sort").list();
        // 不存在，返回错误
        if (shopTypes == null) {
            return Result.fail("分类不存在");
        }
        // 将查询的分类存入redis
        stringRedisTemplate.opsForValue().set("shopTye", JSONUtil.toJsonStr(shopTypes));

        return Result.ok(shopTypes);
    }
}
