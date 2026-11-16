package com.zhuofan.core.kafka;

import com.alibaba.fastjson.JSONObject;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonLogger;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.concurrent.Future;

public class KafkaClient {
    private String kafkaServer;
    private String kafkaPort;
    private String kafkaChatid;
    private String kafkaTopic;
    private Properties properties;
    private String elinkAppname;

    public String getElinkAppname() {
        return elinkAppname;
    }

    public void setElinkAppname(String elinkAppname) {
        this.elinkAppname = elinkAppname;
    }

    public String getKafkaServer() {
        return kafkaServer;
    }

    public void setKafkaServer(String kafkaServer) {
        this.kafkaServer = kafkaServer;
    }

    public String getKafkaPort() {
        return kafkaPort;
    }

    public void setKafkaPort(String kafkaPort) {
        this.kafkaPort = kafkaPort;
    }

    public String getKafkaChatid() {
        return kafkaChatid;
    }

    public void setKafkaChatid(String kafkaChatid) {
        this.kafkaChatid = kafkaChatid;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public void setKafkaTopic(String kafkaTopic) {
        this.kafkaTopic = kafkaTopic;
    }

    //构造函数：使用默认配置参数
    public KafkaClient(){
        this.kafkaServer=CommonFunc.getConfigByKey("sys.kafka_server");
        this.kafkaPort=CommonFunc.getConfigByKey("sys.kafka_port");
        this.kafkaChatid=CommonFunc.getConfigByKey("sys.kafka_chatid");
        this.kafkaTopic=CommonFunc.getConfigByKey("sys.kafka_topic");
        this.elinkAppname=CommonFunc.getConfigByKey("sys.kafka_appname");
        setProperty();
    }

    //构造函数：使用默认配置参数，但需指定appname(模块名）
    public KafkaClient(String moduleName){
        this.kafkaServer=CommonFunc.getConfigByKey("sys.kafka_server");
        this.kafkaPort=CommonFunc.getConfigByKey("sys.kafka_port");
        this.kafkaChatid=CommonFunc.getConfigByKey("sys.kafka_chatid");
        this.kafkaTopic=CommonFunc.getConfigByKey("sys.kafka_topic");
        this.elinkAppname=StringUtils.isNotEmpty(moduleName)?moduleName:CommonFunc.getConfigByKey("sys.kafka_appname");
        setProperty();
    }

    //构造函数
    public KafkaClient(String kafkaServer,String kafkaPort,String kafkaChatid,String kafkaTopic,String elinkAppname)
    {
        this.kafkaServer=kafkaServer;
        this.kafkaPort=kafkaPort;
        this.kafkaChatid=kafkaChatid;
        this.kafkaTopic=kafkaTopic;
        this.elinkAppname=elinkAppname;
        setProperty();
    }

    private void setProperty(){
        // 配置Kafka生产者属性
        properties = new Properties();
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 3000);

        properties.put("bootstrap.servers", kafkaServer+":"+kafkaPort); // 替换为你的Kafka broker地址和端口
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    }

    //1.消息，2：file,3:image
    private boolean sendElinkMsgImpl(String  msgType,String msg,String userList,String chatId){
        JSONObject json=new JSONObject();
        JSONObject jsonRecv=new JSONObject();
        if (StringUtils.isNotEmpty(userList)) {
            jsonRecv.put("user_list", userList);
        }else{
            jsonRecv.put("chat_id",chatId);
        }

        json.put("type","eLink");
        json.put("app_name",elinkAppname);
        json.put("receiver_list",jsonRecv);

        if ("msg".equals(msgType)) {
            json.put("message", msg);
        }else{
            json.put("filetype",msgType);
            json.put("filename",msg);
        }

        return sendMessage(properties,json.toString());
    }

    //发送ELINK文本信息
    public boolean sendElinkMsg(String msg,String userList,String chatId){
        return sendElinkMsgImpl("msg",msg,userList,chatId);
    }

    //发送Elink图片信息
    public boolean sendElinkImage(String filename,String userList,String chatId){
        return sendElinkMsgImpl("image",filename,userList,chatId);
    }

    //发送ELINK文件信息
    public boolean sendElinkFile(String filename,String userList,String chatId){
        return sendElinkMsgImpl("file",filename,userList,chatId);
    }

    //发送手机短信
    public boolean sendSms(String msg,String phoneList){
        JSONObject json=new JSONObject();
        JSONObject jsonRecv=new JSONObject();
        jsonRecv.put("phone_list", phoneList);

        json.put("type","sms");
        json.put("app_name",elinkAppname);
        json.put("receiver_list",jsonRecv);
        json.put("message", msg);

        return sendMessage(properties,json.toString());
    }

    //拔打语音电话
    /**
     * @Author fuxiaodong
     * @Description //TODO
     * @Version: v1.8
     * @Date 15:47 2024/12/27
     * @Param [arrPhoneList, wavFilename, Stringm,taskId]
     * arrPhoneList：拔打的电话，格式为：["号码1"],["号码2"]
     * wavFilename：传入的wav文件名称，不需要带路径，如1.wav
     * repNum：重复拔打次数，必须大于0，不验证，调用时自行把控
     * taskId：本次任务的ID，以后查询状态使用
     * @return boolean
     */
    public Future<RecordMetadata> callTel(String[] arrPhoneList,String wavFilename,String repNum,
                           String taskId,String callType){

        //2025-06-10：主网传9154,配网9252，如不指定ivrnum，默认使用主网拔号
        //后续建议将电话号码放入配置文件中
        return callTel(arrPhoneList,wavFilename,repNum,taskId,"9154",callType);
    }

    public Future<RecordMetadata> callTel(String[] arrPhoneList,String wavFilename,String repNum,
                                          String taskId){

        //2025-06-10：主网传9154,配网9252，如不指定ivrnum，默认使用主网拔号
        //后续建议将电话号码放入配置文件中
        return callTel(arrPhoneList,wavFilename,repNum,taskId,"9154","1");
    }

    //主网语音电话
    public Future<RecordMetadata> callTelZw(String[] arrPhoneList,String wavFilename,String repNum,
                                          String taskId){

        //2025-06-10：主网传9154,配网9252
        //后续建议将电话号码放入配置文件中
        return callTel(arrPhoneList,wavFilename,repNum,taskId,"9154","1");
    }

    //主网语音电话
    public Future<RecordMetadata> callTelZw(String[] arrPhoneList,String wavFilename,String repNum,
                                            String taskId,String callType){

        //2025-06-10：主网传9154,配网9252
        //后续建议将电话号码放入配置文件中
        return callTel(arrPhoneList,wavFilename,repNum,taskId,"9154",callType);
    }

    //配网语音电话
    public Future<RecordMetadata> callTelPw(String[] arrPhoneList,String wavFilename,String repNum,
                                            String taskId ){

        //2025-06-10：配网9252
        //后续建议将电话号码放入配置文件中
        return callTel(arrPhoneList,wavFilename,repNum,taskId,"9252","1");
    }

    //配网语音电话
    public Future<RecordMetadata> callTelPw(String[] arrPhoneList,String wavFilename,String repNum,
                                            String taskId,String clgnum ,String callType){

        //2025-06-10：配网9252
        //后续建议将电话号码放入配置文件中
        return callTel(arrPhoneList,wavFilename,repNum,taskId,clgnum,callType);
    }

    /**
     * @Author fuxiaodong
     * @Description //TODO ：更新语音电话主配网兼容性问题.主网传电话9154
     * @Version: v1.8
     * @Date 15:56 2025/6/10
     * @Param
     * @return
    */
    public Future<RecordMetadata> callTel(String[] arrPhoneList,String wavFilename,String repNum,
                                          String taskId,String ivrnum,String callType){
        JSONObject json=new JSONObject();
        JSONObject jsonRecv=new JSONObject();

        jsonRecv.put("phone_list", arrPhoneList);

        json.put("type","tel");
        //json.put("app_name",elinkAppname);
        json.put("app_name","zfoms");
        json.put("rep_num",repNum);
        json.put("task_id",taskId);
        json.put("receiver_list",jsonRecv);
        json.put("filename", wavFilename);
        json.put("ivrnum",ivrnum);
        //callType:1：带语音的；2：直接打电话
        json.put("callType",callType);

        return sendMessageReturnFuture(properties,json.toString());
    }

    //创建Elink群聊
    public boolean createElinkGroup(String userList,String msg){
        JSONObject json=new JSONObject();
        JSONObject jsonRecv=new JSONObject();
        jsonRecv.put("create_chat", userList);

        json.put("type","eLink");
        json.put("app_name",elinkAppname);
        json.put("receiver_list",jsonRecv);
        json.put("message", msg);

        return sendMessage(properties,json.toString());
    }

    //发送消息
    private boolean sendMessage(Properties properties, String data){
        final boolean[] result = {false};
        CommonLogger commonLogger=CommonLogger.getInstance();

        // 创建Kafka生产者实例
        System.out.println("11.begin create producer");
        commonLogger.info(String.format("%s begin create producer",
                CommonFunc.unix2Date(CommonFunc.unixTimeStr())));

        System.out.println("22.create producer suss");
        commonLogger.info(String.format("%s begin send PowergridAnalysis data:%s",
                CommonFunc.unix2Date(CommonFunc.unixTimeStr()),data));

        // 创建消息记录，指定主题和消息内容
        System.out.println("33.betin create record");
        commonLogger.info(String.format("%s betin create record",
                CommonFunc.unix2Date(CommonFunc.unixTimeStr())));
        System.out.println("33.record data:"+data);
        ProducerRecord<String, String> record = new ProducerRecord<>("IDC_SEND_MSG", data);
        System.out.println("44.create record suss");
        System.out.println("55.record:"+record.toString());
        commonLogger.info(String.format("%s record:%s",
                CommonFunc.unix2Date(CommonFunc.unixTimeStr()),record.toString()));

        // 发送消息到Kafka
        try (Producer<String, String> producer = new KafkaProducer<>(properties)) {
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception e) {
                    if (metadata==null){
                        System.out.println("66.metadata is null");
                        commonLogger.info(String.format("%s metadata is null",
                                CommonFunc.unix2Date(CommonFunc.unixTimeStr())));
                    }else {
                        System.out.println("66.send?" + metadata.toString());
                        commonLogger.info(String.format("%s send?:%s",
                                CommonFunc.unix2Date(CommonFunc.unixTimeStr()), metadata.toString()));
                    }

                    if (e != null) {
                        System.out.println("77.send err");
                        System.err.println("send error:" + e.getMessage());
                        commonLogger.info(String.format("%s send error:a:%s",
                                CommonFunc.unix2Date(CommonFunc.unixTimeStr()),e.getMessage()));
                    } else {
                        System.out.println("88.send suss");
                        System.out.println("send to " + metadata.partition() + ",offset " + metadata.offset());
                        commonLogger.info(String.format("%s send suss,metadata.partition():%s,offset",
                                CommonFunc.unix2Date(CommonFunc.unixTimeStr()),metadata.partition(),metadata.offset()));
                        result[0] =true;
                    }
                }
            });
        }

        return result[0];
    }

    private Future<RecordMetadata> sendMessageReturnFuture(Properties properties, String data){
        CommonLogger commonLogger=CommonLogger.getInstance();
        // 创建Kafka生产者实例
        commonLogger.info(String.format("%s begin create producer", CommonFunc.unix2Date(CommonFunc.unixTimeStr())));
        Producer<String, String> producer = new KafkaProducer<>(properties);

        commonLogger.info(String.format("%s begin send PowergridAnalysis data:%s", CommonFunc.unix2Date(CommonFunc.unixTimeStr()),data));

        // 创建消息记录，指定主题和消息内容
        commonLogger.info(String.format("%s betin create record", CommonFunc.unix2Date(CommonFunc.unixTimeStr())));
        commonLogger.info("33.record data:"+data);
        ProducerRecord<String, String> record = new ProducerRecord<>("IDC_SEND_MSG", data);
        commonLogger.info("44.create record suss");
        commonLogger.info("55.record:"+ record);
        commonLogger.info(String.format("%s record:%s", CommonFunc.unix2Date(CommonFunc.unixTimeStr()), record));
        // 发送消息到Kafka
        Future<RecordMetadata> sendFuture=null;
        try {
            sendFuture= producer.send(record);
        }catch(Exception e){
            commonLogger.info("sendMessageReturnFuture error:"+e.getMessage());
            e.printStackTrace();
        }
        // 关闭生产者

        producer.close();
        commonLogger.info("sendMessageReturnFuture suss");

        return sendFuture;
    }


    public static void main(String[] args) {
        KafkaClient kafkaClient=new KafkaClient("ZFOMS_测试");

        kafkaClient.sendSms("aaaaaa","guoyonghong4926@ext.gd.csg.cn,404505514@qq.com,wangxianmei0842");
        kafkaClient.sendElinkMsg("bbbbb","guoyonghong4926@ext.gd.csg.cn,404505514@qq.com,wangxianmei0842","");
        kafkaClient.sendElinkMsg("cccccc","","MTK3MDMyNDK50DA1NZE4NINTAF29JXlypXyL7FLW2J4");
        kafkaClient.sendElinkFile("aa.txt","guoyonghong4926@ext.gd.csg.cn,404505514@qq.com,wangxianmei0842","");
        kafkaClient.sendElinkImage("1.png","guoyonghong4926@ext.gd.csg.cn,404505514@qq.com,wangxianmei0842","");
    }
}
