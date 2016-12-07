package cn.wsgwz.gravity.fragment;

/**
 * Created by Jeremy Wang on 2016/12/7.
 */

public enum IsProgressEnum {
    START("开始脚本"),STOP("关闭脚本");
    private String values;

    IsProgressEnum(String values) {
        this.values = values;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }
}
