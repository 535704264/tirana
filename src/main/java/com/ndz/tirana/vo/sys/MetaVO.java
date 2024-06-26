package com.ndz.tirana.vo.sys;

import lombok.Data;

/**
 * 路由显示信息
 *
 */
@Data
public class MetaVO {

    /**
     * 设置该路由在侧边栏和面包屑中展示的名字
     */
    private String title;

    /**
     * 设置该路由的图标，对应路径src/assets/icons/svg
     */
    private String icon;

    public MetaVO() {}

    public MetaVO(String title, String icon) {
        this.title = title;
        this.icon = icon;
    }
}
