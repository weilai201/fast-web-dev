package com.fastweb.core.exception;

import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;

import com.fastweb.core.result.RtnData;

/**
 * 全局异常处理器
 * @author zhang
 *
 */
@ControllerAdvice
class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = Throwable.class)
    @ResponseBody
    public RtnData defaultErrorHandler(Throwable e, HttpServletResponse response) throws Exception {
        //业务类型异常，异常消息会被包装到response的json的message中

        if (e instanceof BusinessException) {
            return RtnData.fail(e.getMessage());
        }

        if (e instanceof BindException) {
            List<ObjectError> list = ((BindException) e).getBindingResult().getAllErrors();
            StringBuffer sb = new StringBuffer();
            Iterator it = list.iterator();

            while (it.hasNext()) {
                ObjectError error = (ObjectError) it.next();
                sb.append(error.getDefaultMessage());
            }
            return RtnData.fail(sb.toString());
        }

        //检查文件上传异常
        if (e instanceof MultipartException) {
            if ("Current request is not a multipart request".equals(e.getMessage())) {
                return RtnData.fail("文件不可以为空");
            }
        }

        //serlvetException
        if (e instanceof ServletException) {
            logger.error("servlet exception:" + e.getMessage());
            return RtnData.fail(e.getMessage());
        }
        if (e instanceof HttpMessageNotReadableException) {
            logger.error("http exception:" + e.getMessage());
            return RtnData.fail(e.getMessage());
        }

        if (e instanceof MethodArgumentNotValidException) {
            logger.error("http exception:" + e.getMessage());
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
            List<ObjectError> list = exception.getBindingResult().getAllErrors();
            StringBuffer sb = new StringBuffer();
            Iterator it = list.iterator();

            while (it.hasNext()) {
                ObjectError error = (ObjectError) it.next();
                sb.append(error.getDefaultMessage());
            }
            return RtnData.fail(sb.toString());
        }

        //如果不是业务类型异常，为未知异常，返回“系统异常”提示信息，日志中打印异常堆栈
        logger.error("system error ", e);
        return RtnData.fail("系统异常");
    }

}