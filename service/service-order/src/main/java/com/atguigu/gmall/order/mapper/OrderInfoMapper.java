package com.atguigu.gmall.order.mapper;


import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Xiaoxin
 * @description 针对表【order_info(订单表 订单表)】的数据库操作Mapper
 * @createDate 2022-09-11 14:34:46
 * @Entity com.atguigu.gmall.order.domain.OrderInfo
 */
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {


    /**
     * 如果是未支付 或者是已结束才可以关闭订单
     * @param orderId
     * @param userId
     * @param processStatus
     * @param orderStatus
     * @param expects
     */
    void updateOrderStatus(@Param("orderId") Long orderId,
                           @Param("userId") Long userId,
                           @Param("processStatus") String processStatus,
                           @Param("orderStatus") String orderStatus,
                           @Param("expects") List<String> expects);

}




