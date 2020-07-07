SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for pr_user_lesson
-- ----------------------------

DROP TABLE IF EXISTS `pr_user_lesson_0`;
CREATE TABLE `pr_user_lesson_0` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `class_course_id` int(11) NOT NULL DEFAULT '0' COMMENT '班级课程ID',
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户ID',
  `order_no` varchar(255) NOT NULL COMMENT '订单号',
  `product_id` int(11) NOT NULL DEFAULT '0' COMMENT '产品ID',
  `class_id` int(11) NOT NULL DEFAULT '0' COMMENT '班级ID',
  `parent_class_id` int(11) NOT NULL DEFAULT '0' COMMENT '父班级ID',
  `lesson_id` int(11) NOT NULL DEFAULT '0' COMMENT '课次ID（对应pe_class_lesson的主键ID）',
  `buy_time` datetime DEFAULT NULL COMMENT '购买时间',
  `buy_status` tinyint(1) DEFAULT NULL COMMENT '购买状态(0:已购买,1:未购买)',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 ( 1: 生效， 0 失效 ) 后期有可能会删除学习路径节点',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除状态 1:已删除；0：未删除',
  `ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '时间戳',
  `create_username` varchar(127) NOT NULL COMMENT '创建人用户名',
  `update_username` varchar(127) NOT NULL COMMENT '更新人用户名',
  `create_uid` varchar(127) NOT NULL COMMENT '创建人账号',
  `update_uid` varchar(127) NOT NULL COMMENT '更新人账号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户与课次关系表';

SET FOREIGN_KEY_CHECKS = 1;

DROP TABLE IF EXISTS `pr_user_lesson_1`;
CREATE TABLE `pr_user_lesson_1` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `class_course_id` int(11) NOT NULL DEFAULT '0' COMMENT '班级课程ID',
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户ID',
  `order_no` varchar(255) NOT NULL COMMENT '订单号',
  `product_id` int(11) NOT NULL DEFAULT '0' COMMENT '产品ID',
  `class_id` int(11) NOT NULL DEFAULT '0' COMMENT '班级ID',
  `parent_class_id` int(11) NOT NULL DEFAULT '0' COMMENT '父班级ID',
  `lesson_id` int(11) NOT NULL DEFAULT '0' COMMENT '课次ID（对应pe_class_lesson的主键ID）',
  `buy_time` datetime DEFAULT NULL COMMENT '购买时间',
  `buy_status` tinyint(1) DEFAULT NULL COMMENT '购买状态(0:已购买,1:未购买)',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 ( 1: 生效， 0 失效 ) 后期有可能会删除学习路径节点',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除状态 1:已删除；0：未删除',
  `ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '时间戳',
  `create_username` varchar(127) NOT NULL COMMENT '创建人用户名',
  `update_username` varchar(127) NOT NULL COMMENT '更新人用户名',
  `create_uid` varchar(127) NOT NULL COMMENT '创建人账号',
  `update_uid` varchar(127) NOT NULL COMMENT '更新人账号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户与课次关系表';

SET FOREIGN_KEY_CHECKS = 1;

DROP TABLE IF EXISTS `pr_user_lesson_2`;
CREATE TABLE `pr_user_lesson_2` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `class_course_id` int(11) NOT NULL DEFAULT '0' COMMENT '班级课程ID',
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户ID',
  `order_no` varchar(255) NOT NULL COMMENT '订单号',
  `product_id` int(11) NOT NULL DEFAULT '0' COMMENT '产品ID',
  `class_id` int(11) NOT NULL DEFAULT '0' COMMENT '班级ID',
  `parent_class_id` int(11) NOT NULL DEFAULT '0' COMMENT '父班级ID',
  `lesson_id` int(11) NOT NULL DEFAULT '0' COMMENT '课次ID（对应pe_class_lesson的主键ID）',
  `buy_time` datetime DEFAULT NULL COMMENT '购买时间',
  `buy_status` tinyint(1) DEFAULT NULL COMMENT '购买状态(0:已购买,1:未购买)',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 ( 1: 生效， 0 失效 ) 后期有可能会删除学习路径节点',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除状态 1:已删除；0：未删除',
  `ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '时间戳',
  `create_username` varchar(127) NOT NULL COMMENT '创建人用户名',
  `update_username` varchar(127) NOT NULL COMMENT '更新人用户名',
  `create_uid` varchar(127) NOT NULL COMMENT '创建人账号',
  `update_uid` varchar(127) NOT NULL COMMENT '更新人账号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户与课次关系表';

SET FOREIGN_KEY_CHECKS = 1;

DROP TABLE IF EXISTS `pr_user_lesson_3`;
CREATE TABLE `pr_user_lesson_3` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `class_course_id` int(11) NOT NULL DEFAULT '0' COMMENT '班级课程ID',
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户ID',
  `order_no` varchar(255) NOT NULL COMMENT '订单号',
  `product_id` int(11) NOT NULL DEFAULT '0' COMMENT '产品ID',
  `class_id` int(11) NOT NULL DEFAULT '0' COMMENT '班级ID',
  `parent_class_id` int(11) NOT NULL DEFAULT '0' COMMENT '父班级ID',
  `lesson_id` int(11) NOT NULL DEFAULT '0' COMMENT '课次ID（对应pe_class_lesson的主键ID）',
  `buy_time` datetime DEFAULT NULL COMMENT '购买时间',
  `buy_status` tinyint(1) DEFAULT NULL COMMENT '购买状态(0:已购买,1:未购买)',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 ( 1: 生效， 0 失效 ) 后期有可能会删除学习路径节点',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除状态 1:已删除；0：未删除',
  `ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '时间戳',
  `create_username` varchar(127) NOT NULL COMMENT '创建人用户名',
  `update_username` varchar(127) NOT NULL COMMENT '更新人用户名',
  `create_uid` varchar(127) NOT NULL COMMENT '创建人账号',
  `update_uid` varchar(127) NOT NULL COMMENT '更新人账号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户与课次关系表';

SET FOREIGN_KEY_CHECKS = 1;






