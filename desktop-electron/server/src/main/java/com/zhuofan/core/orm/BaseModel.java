/**
 * com.zhuofan.core.orm
 *
 *
 *   ver     date           author
 *           2015-05-19      fuxiaodong
 *
 * Copyright (c) 2015, TNT All Rights Reserved.
 * */

package com.zhuofan.core.orm;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * ClassName:BaseModel
 *
 * @author   fuxiaodong
 * @Date     2015-05-19
 */
public class BaseModel {
    private HashMap<String,String> changeInfo=new HashMap<String,String>();
    private Object extraInfo;
    private Object extraInfo1;

    public Object getExtraInfo1() {
        return extraInfo1;
    }

    public void setExtraInfo1(Object extraInfo1){
        this.extraInfo1=extraInfo1;
    }

    public Object getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(Object extraInfo){
        this.extraInfo=extraInfo;
    }

    //增加更新信息
    protected void addChangeInfo(String methodName)
    {
        if (!changeInfo.containsKey(methodName))
        {
            changeInfo.put(methodName, "1");
        }
    }

    //获取更新信息
    public HashMap<String,String> getChangeInfo()
    {
        return this.changeInfo;
    }

    //清除更新信息
    public void clearChangeInfo(Object o)
    {
        Method[] methods=o.getClass().getMethods();
        /*
        for (Method method:methods)
        {
            String methodName=method.getName();
            if (!methodName.substring(0,3).equals("set")) continue;

            try
            {
                method.invoke(o,new Object[]{null});
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        */
        this.changeInfo.clear();
    }
}
