package com.zhuofan.core.common;

import org.apache.commons.codec.digest.DigestUtils;

public class ServerKeyIv {

    private String serverKey;
    private String serverIv;

    public String getServerKey(){
        return this.serverKey;
    }

    public String getServerIv(){
        return this.serverIv;
    }

    public ServerKeyIv(String tempfilePath){
        String time=String.valueOf(CommonFunc.unixTime());
        String rand=CommonFunc.getRandom(8);
        this.serverKey= DigestUtils.md5Hex(time+rand);

        this.serverIv=CommonFunc.getRandom(8)+CommonFunc.getRandom(8);

        String fileName=tempfilePath+"/"+this.serverKey;
        CommonFunc.writeTxtFile(fileName,serverIv);
    }
}
