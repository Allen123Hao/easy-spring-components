package com.allen.component.easyexport;

import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/9/14 15:51
 */
public interface ExportHandler {

    Object handle(ProceedingJoinPoint joinPoint, HttpServletResponse response) throws Throwable ;
}
