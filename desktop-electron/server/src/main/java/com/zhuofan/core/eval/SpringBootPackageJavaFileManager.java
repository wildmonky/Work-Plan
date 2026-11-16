package com.zhuofan.core.eval;

import com.zhuofan.core.util.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.IOException;
import java.util.Set;

public class SpringBootPackageJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {


    private static final Logger log = LoggerFactory.getLogger(SpringBootPackageJavaFileManager.class);
    /**
     * Creates a new instance of ForwardingJavaFileManager.
     *
     * @param fileManager delegate to this file manager
     */
    protected SpringBootPackageJavaFileManager(StandardJavaFileManager fileManager) {
        super(fileManager);
    }

    @Override
    public Iterable<JavaFileObject> list(Location location,
                                         String packageName,
                                         Set<JavaFileObject.Kind> kinds,
                                         boolean recurse
    ) throws IOException
    {
        String jarPackageNamePrefix = "BOOT-INF.classes.";
        if (!SpringUtils.isRunAsJar() || !packageName.contains("com.zhuofan")) {
            jarPackageNamePrefix = "";
        }
        return fileManager.list(location, jarPackageNamePrefix + packageName, kinds, recurse);
    }

}
