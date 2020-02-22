# 应用springboot2，mybaits3开发的前后端分离基础商城项目
* tips：项目我**已经部署到了云端服务器，欢迎给本菜鸡提供宝贵意见~**
* **注册入口：**
[点击直接进入注册](http://47.115.19.41/resources/getotp.html)：http://47.115.19.41/resources/getotp.html

* **本篇README所有内容仅包括基础部分的构建，项目后续优化的历程放在了我的博客内，不会再到README中更新。**
项目后续优化记录：https://blog.csdn.net/qq_31314141/category_9716839.html



* 项目使用项目根据业务的需要分为用户模块，商品模块，交易模块，以及秒杀模块，如要使用本项目，**需要的数据库表可以根据mapper文件里面的字段来手动建立。**    


# 项目分层  
## 1.前端UI
## 2.接入层controller（对应view object，即vo）前端返回的是定义好的通用返回对象，若系统正常则返回vo，有异常则返回异常信息和异常状态码。 
## 3.业务层service（这里的业务层对象根据阿里巴巴的规范命名为了business object，即XxBO）包括用户service，商品service，交易下单service，以及秒杀service。
## 4.数据层dao（对应data object，即do，每一个属性和数据库表一一对应，并且使用mybatis逆向插件自动生成）使用事务保证了数据的一致性。


# 以下是按顺序记录的基本开发流程 



# 一.技术选型 

springboot2,mysql,druid连接池,mybatis整合springboot依赖.lombok插件
**以下是mybatis逆向工程插件配置**：


```xml
	<!--mybatis逆向生成工程插件引入 ：START-->
<plugin>
  <groupId>org.mybatis.generator</groupId>
  <artifactId>mybatis-generator-maven-plugin</artifactId>
  <version>1.3.5</version>
  <dependencies>
    <!--这里core依赖会标红，先放到顶部dependencies标签内下载好再放过来即可-->
    <dependency>
      <groupId>org.mybatis.generator</groupId>
      <artifactId>mybatis-generator-core</artifactId>
      <version>1.3.5</version>
    </dependency>
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>8.0.19</version>
    </dependency>
  </dependencies>
  <executions>
    <execution>
      <id>mybatis generator</id>
      <phase>package</phase>
      <goals>
	<goal>generate</goal>
      </goals>
    </execution>
  </executions>
  <configuration>
    <!--允许移动生成的文件-->
    <verbose>true</verbose>
    <!--允许自动覆盖的文件，第二次生成的时候记得设为false-->
    <overwrite>true</overwrite>
    <configurationFile>
      src/main/resources/mybatis-generator.xml
    </configurationFile>
  </configuration>
</plugin>
	<!--mybatis逆向工程插件引入 ：END-->  
```



# 二.创建数据库及用户信息表，用户密码表，并根据逆向工程插件的配置放置配置文件 #  

路径：src/main/resources/下创建mybatis-generator.xml,编写方式如下：    


```xml
<?xml version="1.0" encoding="UTF-8"?>  
<!DOCTYPE generatorConfiguration
        PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">

<generatorConfiguration>     

    <context id="DB2Tables" targetRuntime="MyBatis3">
        <!--数据库链接地址账号密码-->
        <jdbcConnection driverClass="com.mysql.jdbc.Driver"
                        connectionURL="jdbc:mysql://localhost:3306/seckillboot?serverTimezone=GMT%2B8"
                        userId="root"
                        password="root">
        </jdbcConnection>

        <!--生成DataObject类存放位置,即将user_info和user_password在dataobject下分别建立两个java生成文件-->
        <javaModelGenerator targetPackage="com.seckill.dos" targetProject="src/main/java">
            <property name="enableSubPackages" value="true" />    
            <property name="trimStrings" value="true" />
        </javaModelGenerator>

        <!--生成映射文件存放位置,在resources下的mapping下生成映射文件-->
        <sqlMapGenerator targetPackage="mapping"  targetProject="src/main/resources">
            <property name="enableSubPackages" value="true" />
        </sqlMapGenerator>

        <!--生成Dao类存放位置-->
        <javaClientGenerator type="XMLMAPPER" targetPackage="com.seckill.dao"  targetProject="src/main/java">
            <property name="enableSubPackages" value="true" />
        </javaClientGenerator>



        <!--生成对应表及类名-->
        <!--  enableCountByExample="false"
               enableUpdateByExample="false"
               enableDeleteByExample="false"
               enableSelectByExample="false"
               selectByExampleQueryId="false"
               这些属性是为了使得只生成简单查询的对应文件，去掉复杂查询的生成文件，因为一般开发中不太用的到,在table标签里面设置这些
               属性之后，就可以吧xxxDOExample删除掉
        -->

                <table tableName="user_info" domainObjectName="UserDO"
                       enableCountByExample="false"
                       enableUpdateByExample="false"
                       enableDeleteByExample="false"
                       enableSelectByExample="false"
                       selectByExampleQueryId="false"></table>
                <table tableName="user_password" domainObjectName="UserPasswordDO"
                       enableCountByExample="false"
                       enableUpdateByExample="false"
                       enableDeleteByExample="false"
                       enableSelectByExample="false"
                       selectByExampleQueryId="false" ></table>

    </context>    
</generatorConfiguration>    
```

 


# 三.项目配置文件中指定数据源及实现类 #       
逆向工程已经在项目中分别创建好了dao接口，dos包下dataobject数据表对应类,以及classpath下的mapping文件夹下的mapper映射文件
## 1.在项目启动类上添加如下注解：


```java
@SpringBootApplication(scanBasePackages = {"com.seckill"}) //自动扫描seckill包下面带注解的类，如@Service，@Controller等等      
	@MapperScan("com.seckill.dao") //设置扫描dao接口的包的位置     
	public class App {...}      
```



## 2.在application.properties中添加以下配置：

```bash
 	# 指定mybatis映射文件的位置      
	mybatis.mapper-locations=classpath:mapping/*.xml     

	# 为数据源指定一个名称，并且配置数据源连接     
	spring.datasource.name=seckillboot    
	spring.datasource.url=jdbc:mysql://localhost:3306/seckillboot?serverTimezone=GMT%2B8    
	spring.datasource.username=root    
	spring.datasource.password=ssss1111   

	# 配置数据源实现类为druid   
	spring.datasource.type=com.alibaba.druid.pool.DruidDataSource     
	spring.datasource.driver-class-name=com.mysql.jdbc.Driver    
```

	


# 四.完善项目结构：添加service层和controller层 

先编写controller层代码：    
添加类注解：    
	@Controller("user")    
	@RequestMapping("/user")    
	添加方法注解       
	@RequestMapping("/get")     
    @ResponseBody //返回的是json数据而不是地址  

在controller层中编写的UserController类时可以发现以下问题：  

1.controller方法中要调用Service层（已经@Autowired）的方法获取对象响应给前端页面，但是现在只有数据库表对应的UserDO对象。  

2.我们目前先将service层的getUserById的方法的返回类型定义为UserDO，这个UserDO对象所包含的属性如下：    
 
		

```java
private Integer id;     
private String name;     
private Byte gender;      
private Integer age;       
private String telephone;      
private String registerMode;   
private String thirdPartyId;     
```

 
可以发现属性中并不包含密码，这在我们service中是不允许的，service层必须具有business object对象，所以我们新建一个bos包存放模型对象UserBO，即新包含了用户密码属性的对象UserBo，并在service层创建转换方法，从do转换为bo，这里体现为多封装一个用户密码，代码如下：
  
```java
private UserBO convertFromDataObjectToBusinessObject(UserDO userDO, UserPasswordDO userPasswordDO){  
        if(userDO == null){  
            return null;  
        }  
        UserBO userBO = new UserBO();  
        BeanUtils.copyProperties(userDO, userBO);  
        if(userPasswordDO != null){  
            userBO.setEncrptPassword(userPasswordDO.getEncrptPassword());  
        }  
        return userBO;     
    	}         
```

3.在经过第二步的分析之后，我们可以将编写getUserById返回值调整为UserBo，但在通过userPasswordDOMapper获取UserPasswordDO对象时，我们发现原来的mapper只有从select by primary key的逻辑，所以增加了一个select by user_id字段的方法，根据user_id查询用户。最后通过第二步中的转换方法得到的UserBO返回.         

4.我们回到controller层继续编写代码，我们从service层得到了bo对象，这时候绝对不能直接将bo对象直接响应给前端，一是因为bo对象**携带有用户密码**，非常危险，二是bo对象含有**前端不需要的信息**，不需要回传，所以我们要新建一个视图层vos（viewobject）对象，这个视图层对象只包含前台需要的对象属性：
在controller层中写如下的转换方法，将从service层获得的bo对象转换为vo对象，并最终返回，vo对象只含以下基本属性：id,name,gender,age,telephone。  

```java
		private UserVO convertFromBOToVO(UserBO userBO){  
        if(userBO == null){     
            return null;  
        }  
        UserVO userVO = new UserVO();  
        return userVO;                                   
    	}                
```

   
                      
最后编写的controller层的getUser方法如下：                                

```java
		@RequestMapping("/get")                 
    	@ResponseBody //返回的是json数据而不是地址                    
    	public UserVO getUser(@RequestParam(name="id") Integer id){                     
        	//RequestParam获取页面上用户操作从而传过来的id，然后         
        	//调用service服务获取对应id的用户对象并返回给前端回显                        
        	//但是如下两行代码将加密后密码也返回给了前端，非常危险                         
        	//所以为了这个问题加一了层专门为了视图层而创建的模型对象层viewobject(vio)                   
        	UserBO userBO = userService.getUserById(id);                   
        	//将返回的类型从userModel转换为了userVO，即将核心领域模型对象转化为可供ui使用的viewobject                    
        	UserVO userVO = convertFromBOToVO(userBO);                        
        	return userVO;                      
    	}        
```


# 五. 返回类型进一步优化：异常处理 #
我们使用把返回的uservo转换为一种CommonReturnType的形式来实现归一化的异常处理，具体方法为：  

1.定义一个CommonReturnType类，其内部有两个属性：string的status状态信息（success或者fail），以及object类型的data，正常返回（status=success）时里面是的前端需要的信息，出现错误（status=fail）时则保存了错误的错误码和错误信息。  
	 
2.CommonReturnType类内部定义两个创建自身对象的重载方法create，如果传参时只有data而不带任何的状态码，那么就是success，并通过方法内部再调用第二个重载方法把对应的result封装返回；如果传参时就带状态码和status，那么就会直接调用第二个重载的方法 ，并且result我们可以设置为错误信息.  
	     
3.这一步我们要实现将status为fail时，对CommonReturnType的data的设置，我们可以将其设置为一个错误码，和对应错误码的错误信息,即errCode和errMsg，要实现这个功能，我们可以先定义一个CommonError接口：  

```java
 public interface CommonError {  
    	public int getErrorCode();  
    	public String getErrorMessage();  
    	public CommonError setErrMsg(String errMsg);  
		}  
```


4.我们要创建一个枚举类并实现CommonError接口，内部有errCode和errMsg属性，枚举类对象即为错误状态，创建有参构造函数内含两个参数，  即errCode和errMsg，并且在枚举类中实现好接口中的方法将错误码和错误信息的封装，以及错误信息的设置；  
再创建一个BusinessException类继承Exception并也实现CommonError接口BusinessException和EmBusinessError。都实现了CommonError中定义的方法：  
这样外部不仅可以new BusinessException 也可以new EmBusinessError ，也都可以有对应的errCode和errMsg并且都实现了setErrMsg的方法，这样BusinessException可以将原来EmBusinessError定义的errMsg覆盖掉  

* BusinessException内部有两个重载的构造函数：    
 

```java
	 //直接接收EmBusinessError的传参用于构造业务异常                
     public BusinessException(CommonError commonError){           
        super();                  
        this.commonError = commonError;                         
     }               
     //接收自定义errMsg的方式构造业务异常           
     public BusinessException(CommonError commonError, String errMsg){             
        super();               
        this.commonError = commonError;                                           
        this.commonError.setErrMsg(errMsg);                    
     }     
```

   
5.定义exceptionHandler解决未被controller层吸收的exception异常，我们可以用springboot的异常处理的注解                 
	 @ExceptionHandler(Exception.class)//固定写法，写在要处理exception的方法之上                
     @ResponseStatus(HttpStatus.OK)//将异常置为ok状态，以便于回显异常信息                   
     @ResponseBody //以json形式返回异常信息 返回的是status（fail）以及data（errCode，errMsg）      
	 上述的方法要抽取到BaseController类中，并让UserController继承此类，实现controller层共用代码的抽取     

# 六.实现用户获取手机验证码功能 #   
## 1.usercontroller内创建用户获取otp短信的方法 

```java
//basecontroller内定义public static final String CONTENT_TYPE_FORMED="application/x-www-form-urlencoded";  
    @PostMapping(value = "/getotp", consumes = {CONTENT_TYPE_FORMED})  
    @ResponseBody  
    public CommonReturnType getOtp(@RequestParam(name = "telephone") String telephone){  
        //需要按照一定的规则生成otp验证码  
        Random random = new Random();    
        //[0, 900000)随机数  
        int randomInt = random.nextInt(900000);  
        randomInt += 100000; //[100000, 1000000)  
        String otpCode = String.valueOf(randomInt);  

        //将otp验证码同对应用户的手机号关联,使用httpsession的方式绑定用户的手机号和otpCode  
        httpServletRequest.getSession().setAttribute(telephone, otpCode);//用户的otpCode被用户手机号关联  

        //模拟将otp验证码通过短信通道发送给用户，这里省略这个方式,并替换为打印到控制台的方式  
        System.out.println("telephone = " + telephone + " & otpCode = " + otpCode);//telephone = 13833333333 & otpCode = 47733  
        return CommonReturnType.create(null);  
     }		  
```

## 2.使用前后端分离的方式，可以不在项目内部文件夹中编写html，写getotp.html，代码见getotp.html，重点是ajax异步请求部分
tips：要实现前后端分离还需要允许跨域请求，需要在UserController上面添加@CrossOrigin的springboot注解，允许前端ajax请求跨域。  
	3.使用metronic框架美化getotp.html，引入  
```html
<link href="static/assets/global/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>  
	<link href="static/assets/global/css/components.css" rel="stylesheet" type="text/css"/>  
	<link href="static/assets/admin/pages/css/login.css" rel="stylesheet" type="text/css"/> 
```
并在相应标签内加入class属性即可给页面元素添加样式  
    	


# 七.实现用户注册功能
## 1.在controller层内写用户注册方法 
首先从session内获取验证码和用户接收到的验证码是否相同，合法后进入下一步  
将用户的传参都封装到一个bo对象中，特别注意密码要md5加密，，将对象传到service层的注册方法的参数中，下一步是在service层中编写注册方法  

## 2.在service层编写注册方法  
先判断传过来的bo是否为空，完成参数校验（见下面的步骤）  

方法外编写bo转换为do的私有方法和passwordbo转换为passworddo的方法，分别调用dao层mapper将这两个对象插入数据库的两个数据表，（使用insertSelective可以使用数据库字段默认值）最后返回。 
注意：通过在对应mapper的insertSelective标签中设置keyProperty="id" useGeneratedKeys="true"，可以将插入时自动生成的自增的id取出，转换时的passworddo就可以获取插入的user的id  
## 3.编写前端代码  
register.html具体代码见项目中，注意要将所有页面元素（bo级别的属性）全部获取并判空  。并且在controller上面添加`@CrossOrigin(allowCredentials = "true" ,allowedHeaders = "*")`的注解属性，前端的ajax请求中添加：`xhrFields:{withCredentials:true}，//允许跨域请求`  

在数据库将用户电话设置为唯一索引，并`catch DuplicateKeyException`提示用户不能使用同一手机号重复注册  


# 八.实现用户登录功能，并优化参数校验规则 #
1.在controller层内写用户登录方法  
2.在service层编写验证登录方法，这里去mapper里面增加一个通过手机号获取用户信息的sql，

通过用户的手机号获取用户do，并通过do中的userid获取用户数据库中密码，和用户传进来的密码是否相同，不相同则抛异常即可  
   若用户密码正确则将用户登录的凭证放进session内，并返回成功的commonreturntype  
3.写前端页面，见login.html  

4.优化参数校验，引入hibernate-validator，编写ValidationResult设置其属性为boolean型的：参数是否错误 和map型的errMsg  

编写ValidatorImpl类并实现InitializingBean类以及当中的方法，方法内将将hibernate validator通过工厂化的初始化方式使其实例化，并编写validate方法将错误信息封装到ValidationResult中
   在service层中注入ValidatorImpl，在注册方法中使用validate方法校验userbo，在userbo的属性上添加参数校验的注解即可自动校验，若ValidationResult获取的校验结果为false则抛出异常，bo的注解如下  
	
```java
private Integer id;
    @NotBlank(message = "用户名不能为空")
    private String name;
    @NotNull(message = "没有填写性别")
    private Byte gender;
    @NotNull(message = "没有填写年龄")
    @Min(value = 0, message = "年龄必须大于0")
    @Max(value = 150, message = "年龄必须小于150")
    private Integer age;
    @NotBlank(message = "手机号不能为空")
    private String telephone;
    private String registerMode;
    private String thirdPartyId;
    //不同于do的属性
    @NotBlank(message = "密码不能为空")
    private String encrptPassword;
```

# 九.开始商品（item）模块开发#
## 1.建立商品bo和数据库表 
商品bo的属性包括商品id，名称，价格，库存，描述，销量，商品图片.  
	建立有关商品的数据库表，商品库存要和商品信息分表，因为库存与交易流水有关，还要注意价格字段若设置为decimal时，只有长度大于20时，逆向工程才会生成big decimal的price  
	使用mybatis逆向工程生成do，mapper和mapperxml，注意设置pom文件中自动覆盖设置为false，并且提前给所有insert标签加上keyProperty="id" useGeneratedKeys="true"才能获取插入时的id  
## 2.创建商品service接口，并添加：创建商品、获取商品详情、获取商品列表的功能，这里是创建商品  
创建商品的service层：将传过来的商品bo转化为两个do，然后分别插入数据库即可，并且将插入时获取的商品id赋给插入库存表之前的bo，以便库存表插入时携带有itemid。  
	最后，将插入的两个do再转换为一个插入的bo对象，并且返回它供controller层使用  
	
创建商品的controller层：编写创建商品方法  ：

这里的创建代码很简单，从前端获取需要的所有属性，封装给一个bo对象，调用controller层的创建商品方法，转换为vo对象（商品的vo用到了bo的全部属性）返回commtype.create（创建出来的带id的vo即可）  
	编写前端代码，这里出了bug就是因为数据库逆向生成的属性为double price，导致数据库里面插入的价格一直为零，所以要将数据库里面的价格字段长度改为20才可以正常插入价格字段  
	
## 3.实现获取商品列表的功能 ##  
在商品的mapper中添加select 全部字段的方法,在service层中编写listItem方法，将查询出来的list<do> 中的所有的do对象转换为bo，并且返回这个list<bo>（这里应用的jdk8的stream api）  
	编写controller代码，将调用的service中的方法返回的list\<bo> 中的所有的bo对象转换为vo，并且createlist\<bo>  
	编写商品列表浏览的前端代码，这里前端代码有些复杂，还需研究.并在商品的列表页的每行商品处添加跳转，跳转到获取对应这个商品的商品详情页。  
## 4.实现获取商品明细的功能 
service层根据id获取到商品bo，controller层调用这个方法并且转化为vo即可，这里后台代码比较简单，前端代码较复杂.  


# 十.开始交易模块的开发 #  

## 1.建立交易模型，即orderBO入手，思考需要的属性：有以下属性 ##  
id（交易号）、用户id、商品id、购买数量、订单总价。然后建立数据库表order_info，这里的数据库表属性直接对应orderbo即可，建立完数据库表，用逆向工程生成mapper和do对象  

		
## 2.用户下单过程 ##   
在service层编写orderService并思考需要的方法：现在有了交易表，就从交易表入手思考，得出需要建立新建交易的方法，参数为用户id、商品id、购买数量。编写实现类，   
		这里采取落单减库存的操作，在itemservice内创建减库存的方法，在itemstockmapper对中编写decreaseStock的update sql，方法参数为商品id和购买数量，实现减库存，  
		减完库存我们需要将订单的信息封装到一个orderbo中，注意封装的orderprice为总价需要计算，还有封装的订单id需要由单独的一个方法完成，前八位设置为年月日，最后两位暂且为零 ,其他中间几位一次按step递增，并且因为这个功能要新建一个sequence表，里面记录curretvalue和step，按照step递增并记录订单id的中间几位）到此为止封装完了bo的全部属性，同样将bo转换为do对象，转换完毕加入订单数据表。  
这里还需要实现增加销量的方法，参数为商品的id和购买数量，并在mapper对中添加update的sql。  
至此serveice层编写完毕，返回了一个订单模型  
		
来到controller层，编写创建订单的方法，其中，商品id和商品的购买数量从前台传参即可获得，用户id可以从session中的userbo（之前的登录方法中setattribute有设置过）获得，顺便还可以检查用户是否登录。  
		接下来就是调用order service的create，并返回CommonReturnType.create(orderBO)即可  

前端代码在商品详情页面编写，添加一个购买按钮，给其绑定单击时间添加ajax请求即可,下单成功之后添加reload可以看到下单后库存减少的方法。  
		若没有秒杀模块的需求，项目至此基本的功能已经完成  


# 十一.开始秒杀活动的开发 
## 1.秒杀模型的创建，同样从bo入手
创建的business model模型属性有：秒杀模型的id, 秒杀活动名称， 秒杀开始时间， 秒杀结束时间， 秒杀的商品， 秒杀商品价格 ，然后建立数据库表，这里可以直接对应秒杀bo的属性即可     
	需要注意的是，秒杀数据表的表名不能是【kill】，这里和sql的关键字会冲突，出现了bug。    
	建立好数据表同样应用逆向工程生成对应的秒杀do和秒杀mapper对，这里可以直接在数据表上添加一个已经设置好的商品秒杀来模拟。   
  
## 2.获取商品的秒杀模型 ##   
在新建秒杀service，并且添加：根据商品id获取秒杀模型的方法     
	在数据库mapper对添加根据商品id获得秒杀模型的sql，同样，将查询出来的do转换为bo，之后根据获取的秒杀开始和结束时间来set这个商品对应秒杀模型的状态，最后返回秒杀的bo     

## 3.聚合模型 ##    
将秒杀bo聚合为商品bo的属性之一，此时商品模型就成为了聚合模型，在商品的service层插入代码片段进行秒杀模型的聚合，即itemBo.setKillBO  

##  4.视图层逻辑编写
在itemVO中添加killStatus属性来定义商品秒杀状态为0：表示没有秒杀活动，为1：表示秒杀活动进未开始，为2：表示秒杀进行中    
	继续在vo中添加秒杀价格，秒杀活动模型id，秒杀开始时间的属性，这些都是前端页面需要获取的有用信息，再到itemController中的从商品bo转换为vo的方法中添加代码，    
	根据是否获取到了秒杀模型来给killStatus，秒杀价格，秒杀活动模型id，秒杀开始时间这几个属性赋值，返回重新封装好的vo即可。  
	
## 5.前台代码编写 ##    
根据业务需要修改前台需要显示的值即可，这里注意时间格式的转化，以及倒计时模块的写法   

## 6.修改order订单相关代码契合秒杀业务 ##  
在订单的bo中加入秒杀活动的id属性，意义为若秒杀id非空，则表示以秒杀商品方式下单，且对应的商品价格，订单总价属性也要随着秒杀业务的变化为变化   
	在订单数据表中添加对应的秒杀活动id字段，在do中添加相应的属性，mapper对也添加秒杀活动id的字段，这里手动添加不知道为什么总出错，自动生成就不报错  
	
在service层代码的创建订单的方法的入参中添加秒杀活动id的参数，再方法内添加代码校验活动信息，在订单入库处的代码增加封装秒杀活动id的代码，并且根据秒杀活动的状态获取不同的商品价格  

在controller层创建订单的方法上添加新的前端传过来的入参：killId，并且将required设置为false，即如果前端没有传值就是没有秒杀进行  
前端则实现倒计时，时间到了之后将下单按钮释放，点击按钮才可以发送killId不为null。   
		
                
		

        
		





	
	



	















