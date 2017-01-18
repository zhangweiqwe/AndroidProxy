package cn.wsgwz.gravity.config;

/**
 * Created by Jeremy Wang on 2016/12/20.
 */

public enum EnumAssetsConfig {
    ChongQing_YiDong_TianDao("config/ChongQing_YiDong_TianDao.xml","重庆移动天道"),
    ChongQing_YiDong_2("config/YiDongZhogJiQianZhui.xml","移动终极前缀"),
    ChongQing_YiDong_1("config/YiDongCaiXing.xml","移动彩信"),
    ChongQing_YiDong_3("config/YiDongZhangShangShiPing.xml","移动掌上视频"),
    ChongQing_YiDong_1_S("config/YiDongCaiXingShiPing.xml","移动彩信视频"),
    SiChuan_YiDong_1("config/YiDongShengX.xml","移动圣x"),
    ChongQing_LianTong_1("config/LianTongZhangTing.xml","联通掌厅"),
    ChongQing_LianTong_10155("config/LianTong10155.xml","联通双host(10155.com)"),
    DianXingTv189_1("config/DianXingTv189_1.xml","电信tv189_1"),
    DianXingTv189_2("config/DianXingTv189_2.xml","电信tv189_2");


    private String key;
    private String values;

    EnumAssetsConfig(String key, String values) {
        this.key = key;
        this.values = values;
    }

    public String getKey() {
        return key;
    }

    public String getValues() {
        return values;
    }
}
