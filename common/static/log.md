## logback的使用与logback.xml详解

### logback简介

```text
Logback是由log4j创始人设计的另一个开源日志组件,官方网站： http://logback.qos.ch。它当前分为下面下个模块：
```
- logback-core
 
  是其它两个模块的基础模块
- logback-classic
 
  它是log4j的一个改良版本，同时它完整实现了slf4j API，使你可以很方便地更换成其它日志系统如log4j或JDK14 Logging
- logback-access
  
  访问模块与Servlet容器集成提供通过Http来访问日志的功能
## logback取代log4j的理由
1. 更快的实现：Logback的内核重写了，在一些关键执行路径上性能提升10倍以上。而且logback不仅性能提升了，初始化内存加载也更小了。
2. Logback-classic非常自然实现了SLF4j：Logback-classic实现了SLF4j。在使用SLF4j中，你都感觉不到logback-classic。而且因为logback-classic非常自然地实现了slf4j ， 所 以切换到log4j或者其他，非常容易，只需要提供成另一个jar包就OK，根本不需要去动那些通过SLF4JAPI实现的代码
3. 非常充分的文档 官方网站有两百多页的文档。
4. 自动重新加载配置文件，当配置文件修改了，Logback-classic能自动重新加载配置文件。扫描过程快且安全，它并不需要另外创建一个扫描线程。这个技术充分保证了应用程序能跑得很欢在JEE环境里面
5. 配置文件可以处理不同的情况，开发人员经常需要判断不同的Logback配置文件在不同的环境下（开发，测试，生产）。而这些配置文件仅仅只有一些很小的不同，可以通过,和来实现，这样一个配置文件就可以适应多个环境
6. Filters（过滤器）有些时候，需要诊断一个问题，需要打出日志。在log4j，只有降低日志级别，不过这样会打出大量的日志，会影响应用性能。在Logback，你可以继续 保持那个日志级别而除掉某种特殊情况，如alice这个用户登录，她的日志将打在DEBUG级别而其他用户可以继续打在WARN级别。要实现这个功能只需加4行XML配置。可以参考MDCFIlter 
7. 自动压缩已经打出来的log：RollingFileAppender在产生新文件的时候，会自动压缩已经打出来的日志文件。压缩是个异步过程，所以甚至对于大的日志文件，在压缩过程中应用不会受任何影响
8. 堆栈树带有包版本：Logback在打出堆栈树日志时，会带上包的数据。
9. 自动去除旧的日志文件：通过设置TimeBasedRollingPolicy或者SizeAndTimeBasedFNATP的maxHistory属性，你可以控制已经产生日志文件的最大数量。如果设置maxHistory 12，那那些log文件超过12个月的都会被自动移除。  

## logback的配置
#### Logger

Logger作为日志的记录器，关联到应用对应的context上后，主要用于存放日志对象，也可以定义日志类型、级别。

#### Appender

Appender主要用于指定日志输出的目的地，目的地可以是控制台、文件、远程套接字服务器、 MySQL、PostreSQL、 Oracle和其他数据库、 JMS和远程UNIX Syslog守护进程等.

#### Layout

负责把事件转换成字符串，格式化的日志信息的输出

#### LoggerContext

每个logger 都被关联到一个 LoggerContext，LoggerContext负责制造logger，也负责以树结构排列各logger。其他所有logger可通过org.slf4j.LoggerFactory 类的静态方法getLogger取得。 getLogger方法以 logger名称为参数，用同一名字调用LoggerFactory.getLogger 方法所得到的永远都是同一个logger对象的引用。

#### 有效级别及级别的继承
Logger 可以被分配级别。级别包括：TRACE、DEBUG、INFO、WARN 和 ERROR，定义于ch.qos.logback.classic.Level类。如果 logger没有被分配级别，那么它将从有被分配级别的最近的祖先那里继承级别。root logger 默认级别是 DEBUG。

#### 打印方法与级别

打印方法决定记录请求的级别。例如，如果 L 是一个 logger 实例，那么，语句 L.info("..")是一条级别为 INFO的记录语句。记录请求的级别在高于或等于其 logger 的有效级别时被称为被启用，否则，称为被禁用。记录请求级别为 p，其 logger的有效级别为 q，只有则当 p>=q时，该请求才会被执行。

```text
 logback 级别排序为： TRACE < DEBUG < INFO < WARN < ERROR
```
## logback的默认配置

logback默认配置的步骤：
```text
1. 尝试在 classpath下查找文件logback-test.xml
2. 如果文件不存在，则查找文件logback.xml
3. 如果两个文件都不存在，logback用BasicConfigurator自动对自己进行配置，这会导致记录输出到控制台
```
**小结：**
 如果配置文件 logback-test.xml 和 logback.xml 都不存在，那么 logback 默认地会调用BasicConfigurator ，创建一个最小化配置。最小化配置由一个关联到根 logger 的ConsoleAppender 组成。输出用模式为%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n 的 PatternLayoutEncoder 进行格式化。root logger 默认级别是 DEBUG。

## logback.xml常用配置

*根节点<configuration>* 
```xml
<configuration scan="true" scanPeriod="60 seconds" debug="false"> 
　　  <!--其他配置省略--> 
</configuration>　
包含下面三个属性:
scan: 当此属性设置为true时，配置文件如果发生改变，将会被重新加载，默认值为true
scanPeriod: 设置监测配置文件是否有修改的时间间隔，如果没有给出时间单位，默认单位是毫秒。当scan为true时，此属性生效。默认的时间间隔为1分钟
debug: 当此属性设置为true时，将打印出logback内部日志信息，实时查看logback运行状态。默认值为false

```

*子节点<contextName>* 
```xml
<configuration scan="true" scanPeriod="60 seconds" debug="false"> 
     <contextName>myAppName</contextName> 
　　  <!--其他配置省略-->
</configuration>    
说明：
用来设置上下文名称，每个logger都关联到logger上下文，默认上下文名称为default。
但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改

```

*子节点<property>*
```xml
<configuration scan="true" scanPeriod="60 seconds" debug="false"> 
　　　<property name="APP_Name" value="myAppName" /> 
　　　<contextName>${APP_Name}</contextName> 
　　　<!--其他配置省略--> 
</configuration>
说明：
用来定义变量值，它有两个属性name和value，通过<property>定义的值会被插入到logger上下文中，可以使“${}”来使用变量
```
*子节点<timestamp>*
```xml
<configuration scan="true" scanPeriod="60 seconds" debug="false"> 
　　<timestamp key="bySecond" datePattern="yyyyMMdd'T'HHmmss"/> 
　　<contextName>${bySecond}</contextName> 
　　<!-- 其他配置省略--> 
</configuration>
说明：
获取时间戳字符串，他有两个属性key和datePattern:
　key: 标识此<timestamp> 的名字
　datePattern: 设置将当前时间（解析配置文件的时间）转换为字符串的模式，遵循java.txt.SimpleDateFormat的格式

```
*子节点<appender>*
```text
1. 负责写日志的组件，它有两个必要属性name和class。name指定appender名称，class指定appender的全限定名
2. 这个写日志的组件有多个类型
```
 - ConsoleAppender
 ```xml
  <configuration> 
       <!-- 
        1 把日志输出到控制台
        2 <encoder>：对日志进行格式化
        3 <target>：字符串System.out(默认)或者System.err
       -->
  　　　<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender"> 
  　　　　　 <encoder> 
  　　　　　　　　　<pattern>%-4relative [%thread] %-5level %logger{35} - %msg %n</pattern> 
                 <charset>utf8</charset>
  　　　　　 </encoder> 
  　　　</appender> 
  
  　　　<root level="DEBUG"> 
  　　　　　　<appender-ref ref="STDOUT" /> 
  　　　</root> 
  </configuration>
  
 ```
 
 - FileAppender
 ```xml
  <configuration> 
     <!--
       1 把日志添加到文件
       2 <file>：被写入的文件名，可以是相对目录，也可以是绝对目录，如果上级目录不存在会自动创建，没有默认值
       3 <append>：如果是 true，日志被追加到文件结尾，如果是 false，清空现存文件，默认是true
       4 <encoder>：对记录事件进行格式化
       5 <prudent>：如果是 true，日志会被安全的写入文件，即使其他的FileAppender也在向此文件做写入操作，效率低，默认是 false
      -->
  　　<appender name="FILE" class="ch.qos.logback.core.FileAppender"> 
  　　　　<file>testFile.log</file> 
  　　　　<append>true</append> 
  　　　　<encoder> 
  　　　　　　<pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern> 
  　　　　</encoder> 
  　　</appender> 
  
  　　<root level="DEBUG"> 
  　　　　<appender-ref ref="FILE" /> 
  　　</root> 
  </configuration>
 ```
 
 - RollingFileAppender
 ```xml
  <!--
    1 滚动记录文件，先将日志记录到指定文件，当符合某个条件时，将日志记录到其他文件
    2 <file>：被写入的文件名，可以是相对目录，也可以是绝对目录，如果上级目录不存在会自动创建，没有默认值
    3 <append>：如果是 true，日志被追加到文件结尾，如果是 false，清空现存文件，默认是true
    4 <rollingPolicy>:当发生滚动时，决定RollingFileAppender的行为，涉及文件移动和重命名。属性class定义具体的滚动策略类
      class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy" 最常用的滚动策略，它根据时间来制定滚动策略，既负责滚动也负责出发滚动。有以下子节点：
      　　 (1) <fileNamePattern>：必要节点，包含文件名及“%d”转换符，“%d”可以包含一个java.text.SimpleDateFormat指定的时间格式，如：%d{yyyy-MM}。
     如果直接使用 %d，默认格式是 yyyy-MM-dd。
          (2)RollingFileAppender的file子节点可有可无，通过设置file，可以为活动文件和归档文件指定不同位置，当前日志总是记录到file指定的文件（活动文件），活动文件的名字不会改变；
      如果没设置file，活动文件的名字会根据fileNamePattern 的值，每隔一段时间改变一次。“/”或者“\”会被当做目录分隔符。
      　　 (3)<maxHistory>: 可选节点，控制保留的归档文件的最大数量，超出数量就删除旧文件。假设设置每个月滚动，且<maxHistory>是6，则只保存最近6个月的文件，删除之前的旧文件。注意，删除旧文件时，那些为了归档而创建的目录也会被删除。
      还有其他不常用的，可以参看文档
   -->
  <configuration> 
  　　　<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender"> 
  　　　　　　<rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy"> 
  　　　　　　　　　<fileNamePattern>logFile.%d{yyyy-MM-dd}.log</fileNamePattern> 
  　　　　　　　　　<maxHistory>30</maxHistory> 
  　　　　　　</rollingPolicy> 
  　　　　　　<encoder> 
  　　　　　　　　　<pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern> 
  　　　　　　</encoder> 
  　　　</appender> 
  
  　　　<root level="DEBUG"> 
  　　　　　　<appender-ref ref="FILE" /> 
  　　　</root> 
  </configuration>
 ```
 
 - 还有很多，可以参考文档http://logback.qos.ch/documentation.html

*子节点<logger>*
```xml
 <!--
  用来设置某一个包或具体的某一个类的日志打印级别、以及指定<appender>。<logger>仅有一个name属性，一个可选的level和一个可选的addtivity属性。
  可以包含零个或多个<appender-ref>元素，标识这个appender将会添加到这个logger。属性如下：
   name:用来指定受此logger约束的某一个包或者具体的某个类
   level: 用来设置打印级别，大小写无关。TRACE, DEBUG, INFO, WARN, ERROR, ALL和OFF，还有一个特俗值INHERITED或者同义词NULL，代表强制执行上级的级别。 如果未设置此属性，那么当前loger将会继承上级的级别。
   addtivity: 是否向上级logger(root)传递打印信息，默认是true。如果设置true,那么root下的输出源也会输出日志
 -->
<logger name="BUSINESS" level="INFO" additivity="false">
        <appender-ref ref="ALERT_MONITOR"/>
</logger>
``` 

*子节点<root>*
```xml
<!--
 1 它也是<logger>元素，但是它是根logger,是所有<logger>的上级
 2 只有一个level属性,用来设置打印级别，大小写无关：TRACE, DEBUG, INFO, WARN, ERROR, ALL和OFF，不能设置为INHERITED或者同义词NULL。 默认是DEBUG。
-->
<root level="INFO">
        <appender-ref ref="CONSOLE"/>
</root>

```

## 使用步骤
*引入依赖包logback，使用需要和slf4j一起使用，故还需引入slf4j的依赖*
```xml
   <properties>
　　　　<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
　　　　<logback.version>1.1.7</logback.version>
　　　　<slf4j.version>1.7.21</slf4j.version>
　　</properties>

　　<dependencies>
　　　　<dependency>
　　　　　　<groupId>org.slf4j</groupId>
　　　　　　<artifactId>slf4j-api</artifactId>
　　　　　　<version>${slf4j.version}</version>
　　　　　　<scope>compile</scope>
　　　　</dependency>
　　　　<dependency>
　　　　　　<groupId>ch.qos.logback</groupId>
　　　　　　<artifactId>logback-core</artifactId>
　　　　　　<version>${logback.version}</version>
　　　　</dependency>
　　　　<dependency>
　　　　　　<groupId>ch.qos.logback</groupId>
　　　　　　<artifactId>logback-classic</artifactId>
　　　　　　<version>${logback.version}</version>
　　　　　</dependency>
　 </dependencies>
```
*配置logback的xml配置文件*
```xml
logback默认寻找配置文件的规则：
(1) 先到类路径下找名称为logback-test.xml的配置文件
(2) 在类路径下找不到logback-test.xml会继续找logback.xml配置文件
(3) 如果类路径下没有这两个配置文件，那么logback用BasicConfigurator自动对自己进行配置，这会导致记录输出到控制台
注意： 如果和Spring或者SpringBoot整合，可以利用它们提供的配置路径指定logback的位置，不需要再遵循logback的默认路径

<?xml version="1.0" encoding="UTF-8"?>

<configuration scan="true" scanPeriod="10 seconds" debug="true">
    <timestamp key="byDay" datePattern="yyyy-MM-dd"/>
    <property name="LOG_PATH" value="/Users/gentryhuang/Desktop/log"/>

    <property name="PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} %level [%t] %marker %msg%n"/>
    <property name="ALERT_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level marker: %marker。 %message%n%xException%n"/>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>[SM]%date %level %10logger [%file:%line] %msg%n</pattern>
            <charset>utf8</charset>
        </encoder>
    </appender>

    <!-- 添加appender：ALERT_MONITOR 所有的日志都会在生成的文件中 -->
    <appender name="ALERT_MONITOR" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <!-- 如果是tomcat，则是 /opt/logs/tomcat/alert_monitor.log -->
        <file>/opt/logs/spring-boot/alert_monitor.log</file>
        <Append>true</Append>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/opt/logs/spring-boot/bak/alert_monitor.%d{yyyy-MM-dd}.log.zip</fileNamePattern>
            <maxHistory>7</maxHistory>
        </rollingPolicy>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <charset>UTF-8</charset>
            <pattern>${ALERT_PATTERN}</pattern>
        </encoder>
    </appender>
    
    <!-- 异常日志 -->
    <appender name="EXCEPTION_HANDLER" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_PATH}/exception_handler.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/bak/exception_handler.%d{yyyy-MM-dd}.log.zip</fileNamePattern>
            <maxHistory>14</maxHistory>
        </rollingPolicy>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <charset>UTF-8</charset>
            <pattern>${PATTERN}</pattern>
        </encoder>
    </appender>

    <!-- 超时日志 -->
    <appender name="TIME_OUT" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <File>${LOG_PATH}/time_out.log</File>
        <Append>true</Append>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/bak/time_out.%d{yyyy-MM-dd}.log.zip</fileNamePattern>
            <maxHistory>14</maxHistory>
        </rollingPolicy>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <charset>utf-8</charset>
            <pattern>${PATTERN}</pattern>
        </encoder>
    </appender>

    <!-- 时间监控日志 -->
    <appender name="TIME_MONITOR" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_PATH}/time_monitor.log</file>
        <Append>true</Append>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/bak/time_monitor.%d{yyyy-MM-dd}.log.zip</fileNamePattern>
            <maxHistory>5</maxHistory>
        </rollingPolicy>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <charset>UTF-8</charset>
            <pattern>${PATTERN}</pattern>
        </encoder>
    </appender>

    <!-- 业务日志 -->
    <appender name="BUSINESS" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_PATH}/business.log</file>
        <Append>true</Append>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/bak/business.%d{yyyy-MM-dd}.log.zip</fileNamePattern>
            <maxHistory>5</maxHistory>
        </rollingPolicy>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <charset>UTF-8</charset>
            <pattern>${PATTERN}</pattern>
        </encoder>
    </appender>

    <!-- 搜索引擎日志 -->
    <appender name="SOLR" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_PATH}/solr.log</file>
        <Append>true</Append>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/bak/solr.%d{yyyy-MM-dd}.log.zip</fileNamePattern>
            <maxHistory>5</maxHistory>
        </rollingPolicy>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <charset>UTF-8</charset>
            <pattern>${PATTERN}</pattern>
        </encoder>
    </appender>
    
   <!-- 设置输出源 logger（root也是logger,不过它是特殊的logger）,指定名称作为获取的标识 --> 
    <logger name="EXCEPTION_HANDLER" level="INFO" additivity="false">
        <appender-ref ref="EXCEPTION_HANDLER"/>
        <appender-ref ref="ALERT_MONITOR"/>
    </logger>

    <logger name="TIME_OUT" level="INFO" additivity="false">
        <appender-ref ref="TIME_OUT"/>
        <appender-ref ref="ALERT_MONITOR"/>
    </logger>

    <logger name="TIME_MONITOR" level="INFO" additivity="false">
        <appender-ref ref="TIME_MONITOR"/>
        <appender-ref ref="ALERT_MONITOR"/>
    </logger>

    <logger name="SOLR" level="INFO" additivity="false">
        <appender-ref ref="SOLR"/>
        <appender-ref ref="ALERT_MONITOR"/>
    </logger>

    <logger name="BUSINESS1" level="INFO" >
        <appender-ref ref="BUSINESS"/>
        <appender-ref ref="ALERT_MONITOR"/>
    </logger>

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="BUSINESS"/>
    </root>
</configuration>

```
*小结*

```text
1 logback要想输出日志需要配置输出源appender，打日志的logger(子节点)，最好还要配置顶级的logger root。因为在logger下没有输出源的时候，当该logger配置的
  addtivity属性为ture,会把打印日志交给root下的输出源，否则就不输出日志。
2 通过LoggerFactory.getLogger(String loggerName)方法没有找到对应名称的logger时，会使用顶级logger root输出日志  
3 在整合SpringBoot等组件一起使用时，可以根据当前类的Class获取Logger，默认打印到控制台
```
*Logger和Marker一起使用*

```text
一般使用类似org.slf4j.Logger#info(String msg)或info(String format,Object... arg)方法就够用了，但是如果业务过多的话，我们
可以让Marker作为这些方法的一个参数，在打印日志的时候用Marker的值做标记
```
