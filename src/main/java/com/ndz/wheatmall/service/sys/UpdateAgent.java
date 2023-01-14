package com.ndz.wheatmall.service.sys;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.extra.cglib.CglibUtil;
import com.alibaba.fastjson.JSON;
import com.ndz.wheatmall.annotation.History;
import com.ndz.wheatmall.annotation.HistoryRecord;
import com.ndz.wheatmall.dto.org.EmployeeDTO;
import com.ndz.wheatmall.entity.org.EmployeeEntity;
import com.ndz.wheatmall.entity.sys.UpdateHistoryEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 带有历史变更记录的代理类
 */
@Slf4j
@Component
public class UpdateAgent {
    @Transactional
    public void update(BaseDao dao, Object newObj, String bizId){
        // 保存历史变更记录
        compareAndRecordHistory(dao, newObj, bizId);
        // todo 执行业务数据更新
//        dao.update(newObj);
    }

    private void compareAndRecordHistory(BaseDao dao, Object newObj, String bizId) {
        // 模拟数据库取出旧值
        EmployeeEntity old = new EmployeeEntity();
        old.setName("斯科塞斯");
        List<UpdateHistoryEntity> updateHistoryEntities = this.reflectChangFields(old, newObj, bizId);
        System.out.println(JSON.toJSONString(updateHistoryEntities, true));
    }

    /**
     * 目前支持String类型
     */
    private   List<UpdateHistoryEntity>  reflectChangFields(Object oldObj, Object newObj, String bizId){
        // 记录的变更日志
        List<UpdateHistoryEntity> updateHistoryList = new ArrayList<>();

        try {
            // 得到类对象
            Class<? extends Object> oldClass = oldObj.getClass();
            Class<? extends Object> newClass = newObj.getClass();

            // 有History注解才记录
            History historyAnno = newClass.getAnnotation(History.class);
            if (historyAnno==null) return null;

            // 得到属性集合
            Field[] oldFields = oldClass.getDeclaredFields();
            Field[] newFields = newClass.getDeclaredFields();

            for (Field oldfield : oldFields) {
                oldfield.setAccessible(true);  // 设置属性是可以访问的(私有的也可以)
                for (Field newfield : newFields) {
                    newfield.setAccessible(true);   // 设置属性是可以访问的(私有的也可以)
                    if(oldfield.getName().equals(newfield.getName()) && oldfield.getType().equals(newfield.getType())){    // 比较属性名和类型是否一样
                        if(newfield.get(newObj) == null || StringUtils.isEmpty(newfield.get(newObj) + "")){
                            break;    // 属性值为空一样就退出二级循环
                        }
                        if((oldfield.get(oldObj) == null && newfield.get(newObj)!=null) || // 老对象没值
                                oldfield.get(oldObj) !=null && (!oldfield.get(oldObj).equals(newfield.get(newObj)))){ // 老对象有值，比较属性值不一样
                            // 得到需要记录变更字段的注解
                            HistoryRecord hr = newfield.getAnnotation(HistoryRecord.class);
                            if(hr != null){
                                UpdateHistoryEntity updateHistoryEntity = new UpdateHistoryEntity(historyAnno.table(), bizId);
                                String oldVal = null;
                                String newVal = null;
                                if (oldfield.getType().equals(String.class) || oldfield.getType().equals(Integer.class) ){
                                    if (oldfield.get(oldObj) != null) {
                                        oldVal =  (String) oldfield.get(oldObj);
                                    }
                                    newVal = (String) newfield.get(newObj);
                                } else if (oldfield.getType().equals(LocalDateTime.class)) {
                                    if (oldfield.get(oldObj) != null){
                                        oldVal = LocalDateTimeUtil.format((LocalDateTime) oldfield.get(oldObj),DatePattern.NORM_DATETIME_PATTERN);
                                    }
                                    newVal = LocalDateTimeUtil.format((LocalDateTime) newfield.get(newObj),DatePattern.NORM_DATETIME_PATTERN);
                                }
                                updateHistoryEntity.setUpdateFieldVal(hr.field(), oldVal, newVal);
                                updateHistoryList.add(updateHistoryEntity);
                            }
                        }
                    }
                }
            }
            return updateHistoryList;
        } catch (RuntimeException ex) {
            log.error("对象变更记录出错！", ex);
        } catch (Exception ex) {
            log.error("属性内容更改前后验证错误,日志无法被记录！", ex);
        }
        return updateHistoryList;
    }


}
