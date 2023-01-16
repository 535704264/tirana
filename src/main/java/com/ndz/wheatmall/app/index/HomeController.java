package com.ndz.wheatmall.app.index;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ndz.wheatmall.exception.ApiException;
import com.ndz.wheatmall.common.enums.BizCodeEnum;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson2.JSON;
import com.ndz.wheatmall.dto.index.GreetDTO;
import com.ndz.wheatmall.vo.index.GreetVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(tags = "首页模块")
@ApiSupport(author = "535704264@qq.com",order = 1)
@RestController
public class HomeController {

    /**
     * NoApiResponse 注解表示不自动包装
     */
    @GetMapping("/hello")
    // @NoApiResponse
    public String hello() {
        log.debug("debug");
        log.info("info");
        log.warn("warn");
        log.error("error");

        return "HelloWorld";
    }

    /**
     * 返回将会自动包装
     */
    @GetMapping("/greet")
    public GreetVO greet() {
        GreetVO greetVO = new GreetVO();
        greetVO.setMsg("Hello World");
        greetVO.setTime(LocalDateTime.now());
        greetVO.setDate(LocalDate.now());

        // 模拟业务异常
        // if (!JSONUtil.isTypeJSONObject("11111")) {
        // throw new ApiException("业务json不合法");
        // }
        try {
            Integer.valueOf("fdqfqq");
        } catch (Exception ex) {
            throw new ApiException(BizCodeEnum.APP_ERROR, "JSON字符串不合法" + "fdqfqq");
        }

        return greetVO;
    }

    @PostMapping("/say")
    public void say(@RequestBody GreetDTO dto) {
        // 1673677631531 --- 13位毫秒级时间戳
        // curl -H "Content-type: application/json" -X POST -d '{"date":"2023-01-14","time":"2023-01-14 13:48:19"}'
        // localhost:8347/say
        System.out.println(JSON.toJSONString(dto));
    }



}