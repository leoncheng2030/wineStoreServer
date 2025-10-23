-- 微信订阅消息模板初始化SQL
-- 注意：请根据实际申请的微信模板ID替换以下示例数据

-- 清空表数据（可选，仅在需要重新初始化时使用）
-- DELETE FROM wechat_subscription_template WHERE delete_flag = 'N';

-- 插入库存预警通知模板
INSERT INTO wechat_subscription_template (
    id, 
    template_id, 
    template_title, 
    template_content, 
    template_type, 
    status, 
    keyword_config, 
    jump_page, 
    sort_code, 
    ext_json, 
    delete_flag, 
    create_time, 
    create_user, 
    update_time, 
    update_user
) VALUES (
    '1001', 
    'SwafHVIvH0Qdwg2FBwnjiAdsZRxYOkY_mxZ1Fjl_Iek', -- 请替换为实际的模板ID
    '库存预警通知', 
    '设备地址: {{thing2.DATA}}\n商品名称: {{thing4.DATA}}\n设备ID: {{character_string7.DATA}}\n当前库存: {{number5.DATA}}ml', 
    'STOCK_ALERT', 
    'ENABLE', 
    '{"thing2":"设备地址","thing4":"商品名称","character_string7":"设备ID","number5":"当前库存"}', 
    '/pages/device/list', 
    1, 
    '{"description":"设备库存低于预警阈值时发送给代理商和设备管理员"}', 
    'N', 
    NOW(), 
    'system', 
    NOW(), 
    'system'
);

-- 插入订单状态通知模板（示例）
INSERT INTO wechat_subscription_template (
    id, 
    template_id, 
    template_title, 
    template_content, 
    template_type, 
    status, 
    keyword_config, 
    jump_page, 
    sort_code, 
    ext_json, 
    delete_flag, 
    create_time, 
    create_user, 
    update_time, 
    update_user
) VALUES (
    '1002', 
    'YOUR_ORDER_STATUS_TEMPLATE_ID', -- 请替换为实际的模板ID
    '订单状态通知', 
    '标题: {{thing1.DATA}}\n订单号: {{character_string2.DATA}}\n订单状态: {{thing3.DATA}}\n订单金额: {{amount4.DATA}}\n时间: {{time5.DATA}}', 
    'ORDER_STATUS', 
    'DISABLE', -- 默认禁用，需要时启用
    '{"thing1":"标题","character_string2":"订单号","thing3":"订单状态","amount4":"订单金额","time5":"时间"}', 
    '/pages/order/detail', 
    2, 
    '{"description":"订单状态变更时通知用户"}', 
    'N', 
    NOW(), 
    'system', 
    NOW(), 
    'system'
);

-- 插入系统通知模板（示例）
INSERT INTO wechat_subscription_template (
    id, 
    template_id, 
    template_title, 
    template_content, 
    template_type, 
    status, 
    keyword_config, 
    jump_page, 
    sort_code, 
    ext_json, 
    delete_flag, 
    create_time, 
    create_user, 
    update_time, 
    update_user
) VALUES (
    '1003', 
    'YOUR_SYSTEM_NOTICE_TEMPLATE_ID', -- 请替换为实际的模板ID
    '系统通知', 
    '通知标题: {{thing1.DATA}}\n通知内容: {{thing2.DATA}}\n通知时间: {{time3.DATA}}', 
    'SYSTEM_NOTICE', 
    'DISABLE', -- 默认禁用，需要时启用
    '{"thing1":"通知标题","thing2":"通知内容","time3":"通知时间"}', 
    '/pages/index/index', 
    3, 
    '{"description":"系统重要通知推送"}', 
    'N', 
    NOW(), 
    'system', 
    NOW(), 
    'system'
);

-- 备注：
-- 1. template_id 字段需要替换为在微信公众平台申请的真实模板ID
-- 2. keyword_config 字段存储模板参数的JSON配置，用于前端动态生成表单
-- 3. jump_page 字段配置点击消息后跳转的小程序页面路径
-- 4. status 字段控制模板是否启用（ENABLE/DISABLE）
-- 5. template_type 字段用于业务逻辑中按类型查找模板