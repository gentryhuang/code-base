package com.code.exception.demo;

/**
 * @author shunhua
 * @date 2019-09-26
 */
public class Demo2 {
    public static void main(String[] args) {
        Exception exception = new Exception();
        StackTraceElement[] elements = exception.getStackTrace();

        for (int i = 0; i < elements.length; i++) {
            StackTraceElement stackTraceElement = elements[i];
            String className = stackTraceElement.getClassName();
            String methodName = stackTraceElement.getMethodName();
            String fileName = stackTraceElement.getFileName();
            int lineNumber = stackTraceElement.getLineNumber();
            System.out.println("StackTraceElement数组下标 i=" + i + "----------fileName="
                    + fileName + "----------className=" + className + "----------methodName=" + methodName + "----------lineNumber=" + lineNumber);
        }

    }
}
