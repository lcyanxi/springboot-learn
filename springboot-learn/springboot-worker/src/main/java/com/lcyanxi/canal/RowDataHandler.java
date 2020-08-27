package com.lcyanxi.canal;

import com.alibaba.otter.canal.protocol.CanalEntry;

@FunctionalInterface
public interface RowDataHandler {

    /**
     * 把CanalEntry.RowData转化为对应的对象，注意pojo必须和mysql严格对应
     * @param rowData canal同步的数据，对应行
     * @param eventType 操作类型
     * @return
     */
    String changeType(CanalEntry.RowData rowData, CanalEntry.EventType eventType);
}
