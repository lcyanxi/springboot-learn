package com.lcyanxi.canal;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lichang
 * @date 2020/8/27
 */
public class DefaultRowDataHandler implements RowDataHandler {

    /**
     * 默认转化方式，目标对象必须和数据库字段一一对应，目标对象必须是驼峰命名规则
     * @param rowData canal同步的数据，对应行
     * @param eventType 操作类型
     * @return
     */
    @Override
    public String changeType(CanalEntry.RowData rowData, CanalEntry.EventType eventType) {
        Map<String, String> afterCanalMap = new HashMap<>();
        Map<String, String> beforeCanalMap = new HashMap<>();
        List<CanalEntry.Column> rowDataLists = rowData.getAfterColumnsList();
        if (CanalEntry.EventType.DELETE == eventType) {
            rowDataLists = rowData.getBeforeColumnsList();
        }

        dataTransform(rowDataLists,beforeCanalMap);

        if (CanalEntry.EventType.UPDATE == eventType) {
            List<CanalEntry.Column> beforeRowdatatas = rowData.getBeforeColumnsList();
            dataTransform(beforeRowdatatas,beforeCanalMap);
            Map<String,Object> data = Maps.newHashMap();
            data.put("beforeData",beforeCanalMap);
            data.put("afterData",afterCanalMap);

            return JSON.toJSONString(data);
        }

        return JSON.toJSONString(afterCanalMap);
    }

    private void dataTransform(List<CanalEntry.Column> rowDataLists,Map<String, String> canalMap) {
        rowDataLists.forEach(i -> {
            String columnName = i.getName();
            String s = StringUtils.underline2camel(columnName);
            canalMap.put(s, i.getValue());
        });
    }
}
