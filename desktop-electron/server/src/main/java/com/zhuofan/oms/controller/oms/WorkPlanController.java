package com.zhuofan.oms.controller.oms;

import com.alibaba.fastjson.JSON;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.oms.controller.sys.BaseController;
import com.zhuofan.oms.entity.workplan.WorkPlanSendMessageRecord;
import com.zhuofan.oms.enums.workplan.ContentType;
import com.zhuofan.oms.service.workplan.WorkPlanService;
import com.zhuofan.oms.service.workplan2.WorkPlanService2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/work/plan")
public class WorkPlanController extends BaseController {

    @Resource
    private WorkPlanService workPlanService;

    @Resource
    private WorkPlanService2 workPlanService2;

    /**
     * 下载指定文件
     *
     * @param date 日期
     * @param filename 文件名
     * @param response 响应
     */
    @GetMapping("/download/{date}/{filename}")
    public void download(@PathVariable("date") String date,
                         @PathVariable("filename") String filename,
                         @RequestParam(required = false) Boolean isAuto,
                         HttpServletResponse response
    ) throws IOException {
//        if (!CommonFunc.checkIsLogined(request)) {
//            String jsonString = JSON.toJSONString(CommonFunc.makeErrResult("300099"));
//            response.setContentType("application/json");
//            ServletOutputStream outputStream = response.getOutputStream();
//            outputStream.write(jsonString.getBytes());
//            return;
//        }
        ContentType contentType = ContentType.ofType(filename);
        try {
            setResponseHeader(response, contentType.getType() + "." + contentType.getSuffix());
            workPlanService.downloadFile(date, contentType, filename, isAuto, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 上传文件
     *
     * @param files 上传文件列表
     * @return
     */
    @PostMapping("")
    public Map<String, Object> upload(@RequestParam("date") LocalDate date, @RequestParam("type") String type, @RequestParam("files") List<MultipartFile> files) {
//        if (!CommonFunc.checkIsLogined(request)) {
//            return CommonFunc.makeErrResult("300099");
//        }
        ContentType contentType = ContentType.ofType(type);
        if (contentType == null) {
            throw new RuntimeException("未知的文件内容类型");
        }
        workPlanService.saveFile(date, contentType, files);
        return null;
    }

    /**
     * 移除文件
     *
     * @param date     日期
     * @param filename 文件名
     * @return 返回信息
     */
    @GetMapping("/remove/{date}/{filename}")
    public Map<String, Object> remove(@PathVariable("date") LocalDate date, @PathVariable("filename") String filename) {
//        if (!CommonFunc.checkIsLogined(request)) {
//            return CommonFunc.makeErrResult("300099");
//        }
        ContentType contentType = ContentType.ofType(filename);
        if (contentType == null) {
            throw new RuntimeException("未知的文件内容类型");
        }
        workPlanService.removeFile(date, null, contentType, filename);
        return null;
    }

    /**
     * date 对应日期已上传的文件
     * @param date 日期
     * @return 对应日期已上传的文件列表
     */
    @GetMapping("/date/files")
    public List<String> getUploadFiles(@RequestParam("date") LocalDate date,
                                       @RequestParam("type") String type,
                                       @RequestParam(required = false) Boolean isAuto
    ) {
        return workPlanService.getUploadFileNames(date, StringUtils.isBlank(type) ? null : ContentType.ofType(type), isAuto);
    }

    @GetMapping("/analysis/{date}")
    public Map<String, Object> analysis(@PathVariable("date") LocalDate date, @RequestParam("cellColumnName")String cellName) {
//        if (!CommonFunc.checkIsLogined(request)) {
//            return CommonFunc.makeErrResult("300099");
//        }
        try {
            workPlanService.process(date, null, cellName);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonFunc.makeErrResult(e.getMessage());
        }
        return null;
    }

    @GetMapping("/analysis/2/{date}")
    public Map<String, Object> analysis2(@PathVariable("date") LocalDate date, @RequestParam("cellColumnName")String cellName) {
//        if (!CommonFunc.checkIsLogined(request)) {
//            return CommonFunc.makeErrResult("300099");
//        }
        try {
            workPlanService2.process(date, null, cellName);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonFunc.makeErrResult(e.getMessage());
        }
        return null;
    }

    @GetMapping("/analysis/2/send/message")
    public Map<String, Object> analysis2SendMessage(String id, String phone, String message) {
//        if (!CommonFunc.checkIsLogined(request)) {
//            return CommonFunc.makeErrResult("300099");
//        }

        if (StringUtils.isBlank(id) && StringUtils.isAnyBlank(phone, message)) {
            return CommonFunc.makeErrResult("", "电话号码和信息内容不能为空");
        }

        try {
            boolean b = workPlanService2.sendSms(id, phone, message);
            return b ? CommonFunc.makeSussResult(null) : CommonFunc.makeErrResult("", "短信发送失败");
        } catch (Exception e) {
            log.warn(e.getMessage());
            return CommonFunc.makeErrResult(e.getMessage());
        }
    }

    @GetMapping("/analysis/2/message")
    public WorkPlanSendMessageRecord analysis2SendMessage(String id) {
        return workPlanService2.findMessageRecord(id);
    }


}
