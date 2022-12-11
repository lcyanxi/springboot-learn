package com.lcyanxi.manage;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.lcyanxi.model.ResultOrderExportData;
import com.lcyanxi.model.SaleOrder;
import com.lcyanxi.model.TiktokOrder;
import com.lcyanxi.util.ExcelUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TiktokOrderExcelDemo {
    public static void main(String[] args) {
        String path = "C:\\Users\\84589\\Desktop\\bb.xlsx";
        String resultPath = "C:\\Users\\84589\\Desktop\\result.xlsx";
        List<TiktokOrder> tiktokOrders = ExcelUtils.importExcel(path, 0, 1, 0, TiktokOrder.class);
        List<SaleOrder> saleOrders =  ExcelUtils.importExcel(path, 0, 1, 1, SaleOrder.class);
        orderCheck(tiktokOrders, saleOrders,resultPath);

    }

    private static void orderCheck(List<TiktokOrder> tiktokOrders, List<SaleOrder> saleOrders,String resultPath) {
        Map<String, TiktokOrder> tiktokOrderMap = tiktokOrders.stream().peek(item -> item.setOrderNo(item.getOrderNo().replace("\'", ""))).collect(Collectors.toMap(TiktokOrder::getOrderNo, Function.identity()));

        List<ResultOrderExportData> resultOrderExportDataList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(saleOrders)) {
            Map<String, List<SaleOrder>> statisticsMap = saleOrders.stream().peek(item -> item.setOrderNo(item.getOrderNo().replace("A", "")))
                    .collect(Collectors.groupingBy(SaleOrder::getOrderNo));

            statisticsMap.forEach((k, v) -> {
                List<ResultOrderExportData.ProductInfo> productInfos = Lists.newArrayList();
                double sum = v.stream().collect(Collectors.summarizingDouble(SaleOrder::getTotalSaleMoney)).getSum();
                for (SaleOrder saleOrder : v) {
                    ResultOrderExportData.ProductInfo productInfo = ResultOrderExportData.ProductInfo.builder().orderType(saleOrder.getOrderType()).orderStatus(saleOrder.getOrderStatus())
                            .productName(saleOrder.getProductName()).productNum(saleOrder.getProductNum()).saleTime(saleOrder.getSaleTime())
                            .totalSaleMoney(saleOrder.getTotalSaleMoney()).build();
                    productInfos.add(productInfo);
                }

                ResultOrderExportData exportData = ResultOrderExportData.builder().orderNo(k).totalSaleMoney(sum).build();
                TiktokOrder tiktokOrder = tiktokOrderMap.get(k);
                if (Objects.nonNull(tiktokOrder)) {
                    exportData.setServerMoney(tiktokOrder.getServerMoney());
                    exportData.setUserPlayMoney(tiktokOrder.getUserPlayMoney());
                    exportData.setTotalMoney(tiktokOrder.getTotalMoney());
                }
                // == 已收款
                // 抖音付款 > 本店应收款  待调整
                // 抖音付款 < 本店应收款  未收款
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

        List<ResultOrderExportData> exportData = resultOrderExportDataList.stream().sorted(Comparator.comparing(ResultOrderExportData::getStatus)).collect(Collectors.toList());
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
