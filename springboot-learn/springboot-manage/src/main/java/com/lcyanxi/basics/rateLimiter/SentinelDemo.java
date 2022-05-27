package com.lcyanxi.basics.rateLimiter;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2022/05/21/8:41 上午
 */
public class SentinelDemo {
    public static void main(String[] args) {
        // 配置规则
        initFlowRules();

        while (true) {
            try (Entry entry = SphU.entry("helloWorld")) {
                // 被保护的逻辑
                System.out.println("helloWorld");
            } catch (BlockException e) {
                // 处理被流控的逻辑
                System.out.println("blocked");
            }
        }
    }

    private static void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        rule.setResource("helloWorld");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(20);// 设置限制qps 20
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }

}
