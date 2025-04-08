package com.minis.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DefaultObjectMapper implements ObjectMapper {
    String dateFormat = "yyyy-MM-dd";
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);

    String decimalFormat = "#,##0.00";
    DecimalFormat decimalFormatter = new DecimalFormat(decimalFormat);

    public DefaultObjectMapper() {}

    @Override
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
    }

    @Override
    public void setDecimalFormat(String decimalFormat) {
        this.dateFormat = decimalFormat;
        this.decimalFormatter = new DecimalFormat(decimalFormat);
    }

    @Override
    public String writeValuesAsString(Object object) {
        StringBuilder sJsonStr = new StringBuilder("{");
        Class<?> clz = object.getClass();
        Field[] fields = clz.getDeclaredFields();
        // 对返回对象中的每一个属性进行格式转换
        for(Field field : fields) {
            String sField = "";
            Object value = null;
            Class<?> type = null;
            String name = field.getName();
            String strValue = "";
            field.setAccessible(true);
            try {
                value = field.get(object);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            type = field.getType();

            // 针对不同的数据类型进行格式转换
            if(value instanceof Date) {
                LocalDate localDate = ((Date)value).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                strValue = localDate.format(this.dateTimeFormatter);
            } else if (value instanceof BigDecimal || value instanceof Double || value instanceof Float) {
                strValue = decimalFormatter.format(value);
            } else {
                strValue = value.toString();
            }

            // 拼接 Json 串
            if(sJsonStr.toString().equals("{")) {
                sField = "\"" + name + "\":\"" + strValue + "\"";
            } else {
                sField = ",\"" + name + "\":\"" + strValue + "\"";
            }
            sJsonStr.append(sField);
        }
        sJsonStr.append("}");
        return sJsonStr.toString();
    }
}
