-- 添加清空运行和等待队列按钮的函数
insert into T_GF_FUNCATION (func_id, func_code, func_name, is_func, displayorder, url_acction, parent_func_id, appid)
values ('18041', 'MC.current.list.empty', '监控中心>队列监控>运行队列>清空队列', null, null, null, null, 'default');
insert into T_GF_FUNCATION (func_id, func_code, func_name, is_func, displayorder, url_acction, parent_func_id, appid)
values ('18051', 'MC.wait.list.empty', '监控中心>队列监控>等待队列>清空队列', null, null, null, null, 'default');