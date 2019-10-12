# StackTraceElement用法

## 什么是StackTrace

StackTrace(堆栈轨迹)可以认为是一系列方法调用过程的集合。异常处理中常用的printStackTrace()即为打印异常调用的堆栈信息

## StackTraceElement信息

StackTraceElement表示StackTrace(堆栈轨迹)中的一个元素，即一个方法调用过程信息。

```java
/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java.lang;

import java.util.Objects;

/**
 * An element in a stack trace, as returned by {@link
 * Throwable#getStackTrace()}.  Each element represents a single stack frame.
 * All stack frames except for the one at the top of the stack represent
 * a method invocation.  The frame at the top of the stack represents the
 * execution point at which the stack trace was generated.  Typically,
 * this is the point at which the throwable corresponding to the stack trace
 * was created.
 *
 * @since  1.4
 * @author Josh Bloch
 */
public final class StackTraceElement implements java.io.Serializable {
    // Normally initialized by VM (public constructor added in 1.5)
    private String declaringClass;
    private String methodName;
    private String fileName;
    private int    lineNumber;

    /**
     *  构造方法
     * @since 1.5
     */
    public StackTraceElement(String declaringClass, String methodName,
                             String fileName, int lineNumber) {
        this.declaringClass = Objects.requireNonNull(declaringClass, "Declaring class is null");
        this.methodName     = Objects.requireNonNull(methodName, "Method name is null");
        this.fileName       = fileName;
        this.lineNumber     = lineNumber;
    }

    /**
     * 方法调用过程所处的java文件
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 某个方法执行过程涉及的行即方法体中真正涉及到的行
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * 方法调用所在的类全名
     */
    public String getClassName() {
        return declaringClass;
    }

    /**
     * 当前调用方法的名
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Returns true if the method containing the execution point
     * represented by this stack trace element is a native method.
     *
     * @return {@code true} if the method containing the execution point
     *         represented by this stack trace element is a native method.
     */
    public boolean isNativeMethod() {
        return lineNumber == -2;
    }

    /**
     * @see    Throwable#printStackTrace()
     */
    public String toString() {
        return getClassName() + "." + methodName +
            (isNativeMethod() ? "(Native Method)" :
             (fileName != null && lineNumber >= 0 ?
              "(" + fileName + ":" + lineNumber + ")" :
              (fileName != null ?  "("+fileName+")" : "(Unknown Source)")));
    }

    
    public boolean equals(Object obj) {
        if (obj==this)
            return true;
        if (!(obj instanceof StackTraceElement))
            return false;
        StackTraceElement e = (StackTraceElement)obj;
        return e.declaringClass.equals(declaringClass) &&
            e.lineNumber == lineNumber &&
            Objects.equals(methodName, e.methodName) &&
            Objects.equals(fileName, e.fileName);
    }

    /**
     * Returns a hash code value for this stack trace element.
     */
    public int hashCode() {
        int result = 31*declaringClass.hashCode() + methodName.hashCode();
        result = 31*result + Objects.hashCode(fileName);
        result = 31*result + lineNumber;
        return result;
    }

    private static final long serialVersionUID = 6992337162326171013L;
}

```
StackTraceElement被定义为final，可见其作为一个Java的基础类不允许被继承。获取StackTraceElement的方法有两种，均返回StackTraceElement数组
```text
1、Thread.currentThread().getStackTrace()
2、new Throwable().getStackTrace()
```
StackTraceElement数组包含了StackTrace(堆栈轨迹)的内容，通过遍历它可以得到方法间的调用过程，即可以得到当前方法以及其调用者的方法名、调用行数等信息
