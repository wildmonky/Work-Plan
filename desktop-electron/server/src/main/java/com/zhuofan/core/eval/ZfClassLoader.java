/**
 * com.zhuofan.core.eval
 *
 *
 *   ver     date           author
 * ======================================================
 *           2015-05-26      fuxiaodong
 *
 * Copyright (c) 2015, TNT All Rights Reserved.
 * */

package com.zhuofan.core.eval;

import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.util.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.*;
import java.io.File;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;

/**
 * ClassName:zfClassLoader ClassLoader
 *
 * @author fuxiaodong
 * @Date 2015-05-26
 */
public class ZfClassLoader extends ClassLoader
{

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public Class<?> findClass(String className,String str) throws ClassNotFoundException, MalformedURLException {

        String outDir = SpringUtils.getProperty("sys.eval-classpath");
        String jarpath=SpringUtils.getProperty("sys.jar-path");
        String classpath= CommonFunc.getFileList(jarpath,";");
//        classpath=outDir+";"+classpath+";";//+System.getProperty("java.class.path");

//        if (StringUtils.isEmpty(outDir)){
//            outDir = this.getClass().getResource("/").toString().replace("file:/", "");
//        }

        log.info("outDir:{}", outDir);
        outDir = outDir.replaceAll("\\\\", "/");
        //区分linux 和 windows 系统
        outDir= (outDir.startsWith(":/", 1))? outDir : "/" + outDir;

        log.info("outDir:{}", outDir);

        File extraClassDir = new File(outDir);
        if (!extraClassDir.isDirectory()) {
            log.warn("outDir: {}，不是文件夹", outDir);
        }
        if (!extraClassDir.exists() && extraClassDir.mkdirs()) {
            log.error("outDir: {}，文件夹创建失败", outDir);
        }

        //写入到临时文件中
        String javaFileName=outDir+"/"+className+".java";
//		System.out.println(this.getClass().getResource("/"));
//		System.out.println(javaFileName);
        try
        {
            File file=new File(javaFileName);
            if  (!file.exists())
            {
                file.createNewFile();
            }
            FileWriter fileWriter =new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.write(str);
            fileWriter.flush();
            fileWriter.close();
        }catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        // 用于诊断源代码编译错误的对象
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        // 内存中的源代码保存在一个从JavaFileObject继承的类中
        JavaFileObject fileObject = new JavaSourceFromString(javaFileName, str);
        // System.out.println(file);
        Iterable<JavaFileObject> compilationUnits = Collections.singletonList(fileObject);

        String jarPath = SpringUtils.getAppHome();
        log.info("编译资源路径：{}", jarPath);

        //Iterable<String> stringdir = Arrays.asList("-classpath",jarpath+"/dgerms.jar","-d", outDir,"-cp",outDir); // 指定-d dir 参数
        Iterable<String> stringdir = Arrays.asList("-d", outDir, "-cp", jarPath + ";" + jarPath + "!" +"BOOT-INF" + File.separator + "lib;" + jarPath + "!" + "BOOT-INF" + File.separator + "classes"); // 指定-d dir 参数
        log.info("javac 编译参数：{}", stringdir);
        JavaFileManager fm = new SpringBootPackageJavaFileManager(compiler.getStandardFileManager(diagnostics, null, null));

        // 建立一个编译任务
        boolean result=false;
        try
        {
            JavaCompiler.CompilationTask task = compiler.getTask(null, fm, null,
                    stringdir, null, compilationUnits);
            // 编译源程序
            result = task.call();
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        if (result)
        {
            URL extraClassUrl = new File(outDir).toURI().toURL();
//            URL url = new File(jarPath + "\\BOOT-INF\\classes").toURI().toURL();
            ClassLoader urlClassLoader = new URLClassLoader(new URL[]{extraClassUrl}, getClass().getClassLoader());
            return urlClassLoader.loadClass(className);
        }
        return null;
    }

    static class JavaSourceFromString extends SimpleJavaFileObject
    {
        private String name;
        private final String code;

        public JavaSourceFromString(String name, String code)
        {
            super(URI.create("file:///" + name), Kind.SOURCE);
            this.code = code;
        }

        public CharSequence getCharContent(boolean ignoreEncodingErrors)
        {
            return code;
        }
    }

}
