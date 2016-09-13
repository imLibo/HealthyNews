package com.xieyao.healthynews.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * -----------时间工具类----------
 * Created by libo on 2016/2/24.
 */
public class TimeUtil {
    /***
     * 数据库存储的时间戳
     * @return
     */
    public static String getTimeForSave(){
        long time = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result = format.format(new Date(time));
        return result;
    }

    /***
     * 将毫秒数转换为年月日的格式
     * @return
     */
    public static String FormatTimeToYearAndMonth(long timeMills){
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        String result = format.format(new Date(timeMills));
        return result;
    }


}
