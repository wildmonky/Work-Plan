package com.zhuofan.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@ConfigurationProperties(prefix="sys")
@Component
public class SysConfig {
    private String title;
    private String version;

    private String pagetitleLogin;

    private String tempfilePath;

    private String efileSavepath;

    private String efilePdfviewPath;

    private String efilePdfwaterMarkname;

    private String efilePdfwaterPageheader;

    private String efilePdfwaterFoot;

    private String evalClasspath;

    private String jarPath;

    private String clientUrl;

    private String dkmUploadPath;

    private String traceSrcfile;

    private String inspectDatacolDefvalue;

    private String querytsdApiurl;

    private String traceBackupdir;

    private String querytsdexApiurl;

    private String deptidZwdd;

    private String deptidPwdd;

    private String ocsidDgwg;

    private String deptidZdhyb;

    private String deptidZdheb;

    private String ocsidZdfh;

    private String ocsidZgdl;

    private String ocsidDgzdzfh;

    private String importTemplateDir;

    private String traceSrcpath;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = new String(title.getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPagetitleLogin() {
        return pagetitleLogin;
    }

    public void setPagetitleLogin(String pagetitleLogin) {

        this.pagetitleLogin = new String(pagetitleLogin.getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);;
    }

    public String getTempfilePath(){
        return  this.tempfilePath;
    }

    public void setTempfilePath(String tempfilePath){
        this.tempfilePath=tempfilePath;
    }

    public String getEfileSavepath() {
        return efileSavepath;
    }

    public void setEfileSavepath(String efileSavepath) {
        this.efileSavepath = efileSavepath;
    }

    public String getEfilePdfviewPath() {
        return efilePdfviewPath;
    }

    public void setEfilePdfviewPath(String efilePdfviewPath) {
        this.efilePdfviewPath = efilePdfviewPath;
    }

    public String getEfilePdfwaterMarkname() {
        return efilePdfwaterMarkname;
    }

    public void setEfilePdfwaterMarkname(String efilePdfwaterMarkname) {
        this.efilePdfwaterMarkname = new String(efilePdfwaterMarkname.getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
    }

    public String getEfilePdfwaterPageheader() {
        return efilePdfwaterPageheader;
    }

    public void setEfilePdfwaterPageheader(String efilePdfwaterPageheader) {
        this.efilePdfwaterPageheader = new String(efilePdfwaterPageheader.getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
    }

    public String getEfilePdfwaterFoot() {
        return efilePdfwaterFoot;
    }

    public void setEfilePdfwaterFoot(String efilePdfwaterFoot) {
        this.efilePdfwaterFoot = new String(efilePdfwaterFoot.getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
    }

    public String getEvalClasspath() {
        return evalClasspath;
    }

    public void setEvalClasspath(String evalClasspath) {
        this.evalClasspath = evalClasspath;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getClientUrl() {
        return clientUrl;
    }

    public void setClientUrl(String clientUrl) {
        this.clientUrl = clientUrl;
    }

    public String getDkmUploadPath() {
        return dkmUploadPath;
    }

    public void setDkmUploadPath(String dkmUploadPath) {
        this.dkmUploadPath = dkmUploadPath;
    }

    public String getTraceSrcfile() {
        return traceSrcfile;
    }

    public void setTraceSrcfile(String traceSrcfile) {
        this.traceSrcfile = traceSrcfile;
    }

    public String getInspectDatacolDefvalue() {
        return inspectDatacolDefvalue;
    }

    public void setInspectDatacolDefvalue(String inspectDatacolDefvalue) {
        this.inspectDatacolDefvalue =  new String(inspectDatacolDefvalue.getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
    }

    public String getQuerytsdApiurl() {
        return querytsdApiurl;
    }

    public void setQuerytsdApiurl(String querytsdApiurl) {
        this.querytsdApiurl = querytsdApiurl;
    }

    public String getTraceBackupdir() {
        return traceBackupdir;
    }

    public void setTraceBackupdir(String traceBackupdir) {
        this.traceBackupdir = traceBackupdir;
    }

    public String getQuerytsdexApiurl() {
        return querytsdexApiurl;
    }

    public void setQuerytsdexApiurl(String querytsdexApiurl) {
        this.querytsdexApiurl = querytsdexApiurl;
    }

    public String getDeptidZwdd() {
        return deptidZwdd;
    }

    public void setDeptidZwdd(String deptidZwdd) {
        this.deptidZwdd = deptidZwdd;
    }

    public String getOcsidDgwg() {
        return ocsidDgwg;
    }

    public void setOcsidDgwg(String ocsidDgwg) {
        this.ocsidDgwg = ocsidDgwg;
    }

    public String getDeptidZdhyb() {
        return deptidZdhyb;
    }

    public void setDeptidZdhyb(String deptidZdhyb) {
        this.deptidZdhyb = deptidZdhyb;
    }

    public String getDeptidZdheb() {
        return deptidZdheb;
    }

    public void setDeptidZdheb(String deptidZdheb) {
        this.deptidZdheb = deptidZdheb;
    }

    public String getOcsidZdfh() {
        return ocsidZdfh;
    }

    public void setOcsidZdfh(String ocsidZdfh) {
        this.ocsidZdfh = ocsidZdfh;
    }

    public String getOcsidZgdl() {
        return ocsidZgdl;
    }

    public void setOcsidZgdl(String ocsidZgdl) {
        this.ocsidZgdl = ocsidZgdl;
    }

    public String getImportTemplateDir() {
        return importTemplateDir;
    }

    public void setImportTemplateDir(String importTemplateDir) {
        this.importTemplateDir = importTemplateDir;
    }

    public String getTraceSrcpath() {
        return traceSrcpath;
    }

    public void setTraceSrcpath(String traceSrcpath) {
        this.traceSrcpath = traceSrcpath;
    }

    public String getOcsidDgzdzfh() {
        return ocsidDgzdzfh;
    }

    public void setOcsidDgzdzfh(String ocsidDgzdzfh) {
        this.ocsidDgzdzfh = ocsidDgzdzfh;
    }

    public String getDeptidPwdd() {
        return deptidPwdd;
    }

    public void setDeptidPwdd(String deptidPwdd) {
        this.deptidPwdd = deptidPwdd;
    }
}
