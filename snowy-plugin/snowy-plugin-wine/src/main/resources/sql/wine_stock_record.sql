-- 库存记录表
CREATE TABLE wine_stock_record (
    id VARCHAR(255) NOT NULL COMMENT '主键',
    device_id VARCHAR(255) NOT NULL COMMENT '设备ID',
    operation_type VARCHAR(20) NOT NULL COMMENT '操作类型：RESTOCK-补货，CONSUME-消费，ADJUST-调整',
    before_stock INT DEFAULT 0 COMMENT '变更前库存',
    after_stock INT DEFAULT 0 COMMENT '变更后库存', 
    change_quantity INT NOT NULL COMMENT '变更数量（正数为增加，负数为减少）',
    operator_id VARCHAR(255) COMMENT '操作人员ID',
    operator_type VARCHAR(20) COMMENT '操作人员类型：DEVICE_MANAGER-设备管理员，AGENT-代理商，SYSTEM-系统',
    remark VARCHAR(500) COMMENT '备注',
    sort_code INT DEFAULT 0 COMMENT '排序码',
    ext_json TEXT COMMENT '扩展信息',
    delete_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_user VARCHAR(255) COMMENT '创建用户',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    update_user VARCHAR(255) COMMENT '修改用户',
    PRIMARY KEY (id),
    INDEX idx_device_id (device_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_operator_id (operator_id),
    INDEX idx_create_time (create_time)
) COMMENT='库存记录表';