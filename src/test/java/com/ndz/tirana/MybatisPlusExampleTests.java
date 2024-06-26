package com.ndz.tirana;

import java.time.LocalDateTime;

import javax.annotation.Resource;

import com.ndz.tirana.common.enums.demo.CadreRankEnum;
import com.ndz.tirana.dao.org.EmployeeDao;
import com.ndz.tirana.entity.org.EmployeeEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.json.JSONUtil;

@SpringBootTest
@ActiveProfiles("test")
public class MybatisPlusExampleTests {

    @Resource
    EmployeeDao employeeDao;

    @Test
    public void test0() {
        // 新增职员
        EmployeeEntity employeeEntity = new EmployeeEntity();
        employeeEntity.setName("Jack Lee");
        employeeEntity.setDepartment("开发部");
        employeeEntity.setCadreRank(CadreRankEnum.SELECT);
        employeeEntity.setJob("Java Coder");
//        employeeEntity.setPosition(Arrays.asList(PositionEnum.COO, PositionEnum.STAFF));
        employeeEntity.setEntryDateTime(LocalDateTime.now());
        employeeDao.insert(employeeEntity);
    }

    @Test
    public void test01() {
        // 查询部分字段
        LambdaQueryWrapper<EmployeeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(EmployeeEntity::getEmpId, EmployeeEntity::getName, EmployeeEntity::getCadreRank).eq(EmployeeEntity::getEmpId, "1");
        EmployeeEntity employeeEntity = employeeDao.selectOne(queryWrapper);
        System.out.println(JSONUtil.toJsonStr(employeeEntity));
    }

    @Test
    public void test02() {
        // 默认查询所有字段
        EmployeeEntity employeeEntity = employeeDao.selectById("1");
        System.out.println(JSONUtil.toJsonStr(employeeEntity));
    }

    @Test
    public void test03() {
        // 分页（单表）
        Page<EmployeeEntity> page = employeeDao.selectPage(new Page<>(1, 10), new LambdaQueryWrapper<>());
        System.out.println(JSONUtil.toJsonStr(page));
    }
}
