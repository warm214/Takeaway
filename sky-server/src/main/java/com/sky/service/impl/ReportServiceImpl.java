package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    UserMapper userMapper;
    /**
     *统计指定时间区间内的营业额数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin=begin.plusDays(1);//计算日期的函数
            dateList.add(begin);
        }
        String join = StringUtils.join(dateList, ",");
        List<Double> turnoverList=new ArrayList<>();
        for (LocalDate date : dateList) {
            //查询当日已完成订单金额的合计
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //获取当天的营业额
            Map map=new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover=orderMapper.sumAmount(map);
            turnover=turnover==null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }
        String join1 = StringUtils.join(turnoverList, ",");

        return TurnoverReportVO
                .builder()
                .dateList(join)
                .turnoverList(join1)
                .build();
    }

    /**
     *统计指定时间区间内的用户数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin=begin.plusDays(1);//计算日期的函数
            dateList.add(begin);
        }
        String join = StringUtils.join(dateList, ",");
        //每天新增用户数量
        List<Integer> newUserList=new ArrayList<>();
        //总用户数量
        List<Integer> totalUserList=new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map=new HashMap();
            map.put("end",endTime);
            //截至到endTime时候的总用户数量
            Integer totalUser = userMapper.countByMap(map);
            map.put("begin",beginTime);
            //当天新增的用户数量
            Integer newUser = userMapper.countByMap(map);
            newUserList.add(newUser);
            totalUserList.add(totalUser);
        }
        String totalUserString = StringUtils.join(totalUserList, ",");
        String newUserString = StringUtils.join(newUserList, ",");

        return UserReportVO.builder()
                .dateList(join)
                .totalUserList(totalUserString)
                .newUserList(newUserString)
                .build();

    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin=begin.plusDays(1);//计算日期的函数
            dateList.add(begin);
        }

        //每日订单数，以逗号分隔，例如：260,210,215
        List<Integer> orderCountList=new ArrayList<>();
        //每日有效订单数，以逗号分隔，例如：20,21,10
        List<Integer> validOrderCountList=new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer orderCount = getOrderCount(beginTime, endTime, null);
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);
            orderCountList.add(orderCount);

            validOrderCountList.add(validOrderCount);
        }
        String dateString = StringUtils.join(dateList, ",");
        String orderCountString = StringUtils.join(orderCountList, ",");
        String validOrderCountString = StringUtils.join(validOrderCountList, ",");

        //订单总数
        Integer totalOrderCount=orderCountList.stream().reduce(Integer::sum).get();

        //有效订单数
        Integer validOrderCount=validOrderCountList.stream().reduce(Integer::sum).get();

        //订单完成率
        Double orderCompletionRate=0.0;
        if (totalOrderCount!=0){
            orderCompletionRate=validOrderCount.doubleValue()/totalOrderCount;

        }




        return OrderReportVO.builder()
                .dateList(dateString)
                .orderCountList(orderCountString)
                .validOrderCountList(validOrderCountString)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }
    private Integer getOrderCount(LocalDateTime begin,LocalDateTime end,Integer status){
        Map map=new HashMap();
        map.put("begin",begin);
        map.put("end",end);
        map.put("status",status);
        return orderMapper.countOrder(map);

    }

    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, endTime);


        List<String> nameList = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameListString = StringUtils.join(nameList, ",");

        List<Integer> numberList = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberListString = StringUtils.join(numberList, ",");

        return SalesTop10ReportVO.builder()
                .nameList(nameListString)
                .numberList(numberListString)
                .build();
    }
}






































