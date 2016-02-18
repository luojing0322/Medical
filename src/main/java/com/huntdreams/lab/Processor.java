package com.huntdreams.lab;

import org.apache.log4j.Logger;

import java.io.File;

/**
 * Base Processor
 *
 * @author tyee.noprom@qq.com
 * @time 2/18/16 12:31 PM.
 */
public class Processor {

    // logger
    protected Logger logger = Logger.getLogger(getClass().getName());

    // docpath
    protected String docPath;

    // init
    public Processor() {
        // 初始化路径信息
        File classPath = new File(this.getClass().getResource("/").getPath());
        File targetPath = new File(classPath.getParent());
        File docPath = new File(targetPath.getParent());
        this.docPath = docPath.getAbsolutePath() + "/doc";
    }
}