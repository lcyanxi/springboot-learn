package com.lcyanxi.manage.xiaoxi;

import java.io.FileOutputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.lcyanxi.model.xiaoxi.TikTokOrderEnter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;

import com.lcyanxi.model.ResultOrderExportData;
import com.lcyanxi.model.SaleOrder;
import com.lcyanxi.model.TiktokOrder;
import com.lcyanxi.util.ExcelUtils;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;

public class TiktokOrderExcelDemo {
    public static void main(String[] args) {
        String path = "/Users/lichang/Desktop/cc.xlsx";
        String resultPath = "/Users/lichang/Desktop/result.xlsx";
        List<TikTokOrderEnter> tiktokOrders = ExcelUtils.importExcel(path, 0, 1, 0, TikTokOrderEnter.class);
        tiktokOrderProcess(resultPath, tiktokOrders);
        System.out.println(tiktokOrders);
        // List<SaleOrder> saleOrders = ExcelUtils.importExcel(path, 0, 1, 1, SaleOrder.class);
        // orderCheck(tiktokOrders, saleOrders,resultPath);

    }

    private static void tiktokOrderProcess(String resultPath, List<TikTokOrderEnter> tiktokOrders) {
        if (CollectionUtils.isEmpty(tiktokOrders)) {
            return;
        }

        // 仅退款 、 售后退货


        Map<String, List<TikTokOrderEnter>> listMap = tiktokOrders.stream().map(item -> {
            if ("仅退款".equals(item.getOrderType())) {
                item.setGroupId("1");
            } else if ("售后退货".equals(item.getOrderType())) {
                item.setGroupId("2");
            } else {
                item.setGroupId("3");
            }
            return item;
        }).collect(Collectors.groupingBy(TikTokOrderEnter::getGroupId));

        List<TikTokOrderEnter> tuiKuangs = dataUtil(listMap.getOrDefault("1", Lists.newArrayList()));
        List<TikTokOrderEnter> tuiHuos = dataUtil(listMap.getOrDefault("2", Lists.newArrayList()));
        List<TikTokOrderEnter> trueEnters = dataUtil(listMap.getOrDefault("3", Lists.newArrayList()));

        trueEnters.addAll(tuiHuos);
        trueEnters.addAll(tuiKuangs);
        try {
            ExportParams exportParams = new ExportParams(null, null, "匹配");
            exportParams.setType(ExcelType.XSSF);
            Workbook workbook = ExcelExportUtil.exportExcel(exportParams, TikTokOrderEnter.class, trueEnters);
            FileOutputStream fos = new FileOutputStream(resultPath);
            workbook.write(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static List<TikTokOrderEnter> dataUtil(List<TikTokOrderEnter> enters) {
        Map<String, List<TikTokOrderEnter>> listMap = enters.stream().peek(item -> {
            if (StringUtils.isBlank(item.getOrderNo())) {
                item.setOrderNo(item.getOrderCode());
            }
        }).collect(Collectors.groupingBy(TikTokOrderEnter::getOrderNo));

        List<TikTokOrderEnter> orderEnters = Lists.newArrayList();
        listMap.forEach((key, list) -> {
            TikTokOrderEnter orderEnter = TikTokOrderEnter.builder().orderNo(key).build();
            orderEnter.setNum(list.stream().mapToInt(TikTokOrderEnter::getNum).sum());
            orderEnter.setName(list.stream().map(TikTokOrderEnter::getName).collect(Collectors.joining(",")));
            orderEnter.setProductCode(
                    list.stream().map(TikTokOrderEnter::getProductCode).collect(Collectors.joining(",")));
            orderEnter.setOrderType(
                    list.stream().map(TikTokOrderEnter::getOrderType).distinct().collect(Collectors.joining(",")));
            orderEnter.setTotalMoney(list.stream().mapToDouble(TikTokOrderEnter::getTotalMoney).sum());
            orderEnter.setOrderCode(
                    list.stream().map(TikTokOrderEnter::getOrderCode).distinct().collect(Collectors.joining(",")));
            orderEnter.setSendTime(list.stream().min(Comparator.comparing(TikTokOrderEnter::getSendTime))
                    .map(TikTokOrderEnter::getSendTime).orElse(null));
            orderEnter.setOrderTime(list.stream().min(Comparator.comparing(TikTokOrderEnter::getOrderTime))
                    .map(TikTokOrderEnter::getOrderTime).orElse(null));
            orderEnter.setOrigin(
                    list.stream().map(TikTokOrderEnter::getOrigin).distinct().collect(Collectors.joining(",")));
            orderEnter.setOrderOrigin(
                    list.stream().map(TikTokOrderEnter::getOrderOrigin).distinct().collect(Collectors.joining(",")));
            orderEnters.add(orderEnter);
        });
        return orderEnters;
    }

    private static void orderCheck(List<TiktokOrder> tiktokOrders, List<SaleOrder> saleOrders, String resultPath) {
        Map<String, TiktokOrder> tiktokOrderMap =
                tiktokOrders.stream().peek(item -> item.setOrderNo(item.getOrderNo().replace("\'", "")))
                        .collect(Collectors.toMap(TiktokOrder::getOrderNo, Function.identity()));

        List<ResultOrderExportData> resultOrderExportDataList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(saleOrders)) {
            Map<String, List<SaleOrder>> statisticsMap =
                    saleOrders.stream().peek(item -> item.setOrderNo(item.getOrderNo().replace("A", "")))
                            .collect(Collectors.groupingBy(SaleOrder::getOrderNo));

            statisticsMap.forEach((k, v) -> {
                List<ResultOrderExportData.ProductInfo> productInfos = Lists.newArrayList();
                double sum = v.stream().collect(Collectors.summarizingDouble(SaleOrder::getTotalSaleMoney)).getSum();
                for (SaleOrder saleOrder : v) {
                    ResultOrderExportData.ProductInfo productInfo = ResultOrderExportData.ProductInfo.builder()
                            .orderType(saleOrder.getOrderType()).orderStatus(saleOrder.getOrderStatus())
                            .productName(saleOrder.getProductName()).productNum(saleOrder.getProductNum())
                            .saleTime(saleOrder.getSaleTime())
                            .totalSaleMoney(saleOrder.getTotalSaleMoney()).build();
                    productInfos.add(productInfo);
                }

                ResultOrderExportData exportData =
                        ResultOrderExportData.builder().orderNo(k).totalSaleMoney(sum).build();
                TiktokOrder tiktokOrder = tiktokOrderMap.get(k);
                if (Objects.nonNull(tiktokOrder)) {
                    exportData.setServerMoney(tiktokOrder.getServerMoney());
                    exportData.setUserPlayMoney(tiktokOrder.getUserPlayMoney());
                    exportData.setTotalMoney(tiktokOrder.getTotalMoney());
                }
                // == 已收款
                // 抖音付款 > 本店应收款 待调整
                // 抖音付款 < 本店应收款 未收款
                String status = "";
                if (exportData.getTotalSaleMoney() == exportData.getUserPlayMoney()) {
                    status = "已收款";
                } else if (exportData.getTotalSaleMoney() < exportData.getUserPlayMoney()) {
                    status = "待调整";
                } else if (exportData.getTotalSaleMoney() > exportData.getUserPlayMoney()) {
                    status = "未收款";
                }
                exportData.setStatus(status);
                exportData.setInfos(productInfos);
                resultOrderExportDataList.add(exportData);
            });
        }

        List<ResultOrderExportData> exportData = resultOrderExportDataList.stream()
                .sorted(Comparator.comparing(ResultOrderExportData::getStatus)).collect(Collectors.toList());
        try {
            ExportParams exportParams = new ExportParams(null, null, "匹配");
            exportParams.setType(ExcelType.XSSF);
            Workbook workbook = ExcelExportUtil.exportExcel(exportParams, ResultOrderExportData.class, exportData);
            FileOutputStream fos = new FileOutputStream(resultPath);
            workbook.write(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
