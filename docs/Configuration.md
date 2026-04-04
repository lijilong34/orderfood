# 本项目需要修改的地方
1. 修改短信验证api<br>
路径:orderfoodafter\src\main\java\org\example\orderfoodafter\entity\Mobile.java<br>
去[互亿无线](https://www.ihuyi.com/)申请api,并修改
2. 修改支付宝api<br>
路径:orderfoodafter\src\main\java\org\example\orderfoodafter\config\AlipayConfig.java<br>
去[支付宝开放平台](https://open.alipay.com/)申请api修改
```java
 app_id=""
 merchant_private_key=""
 alipay_public_key=""
```
3.智普图片生成描述api
路径:
<br>\orderfoodafter\src\main\java\org\example\orderfoodafter\service\impl\AiServiceImpl.java<br>
去[智普开放平台](https://open.bigmodel.cn/)申请api
```java
    private static final ZhipuAiClient zhipuClient = ZhipuAiClient.builder()
            .apiKey("")
            .build();
    private static final ZhipuAiClient imageClient = ZhipuAiClient.builder()
            .apiKey("")
            .build();
```
双引号填入api key<br>
4.总ai,填入以下信息
```properties
#api密钥
langchain4j.open-ai.chat-model.api-key=
#模型名称 
langchain4j.open-ai.chat-model.model-name=
#请求地址
langchain4j.open-ai.chat-model.base-url=
langchain4j.open-ai.chat-model.log-requests=true
langchain4j.open-ai.chat-model.log-responses=true
langchain4j.open-ai.chat-model.temperature=0.1
langchain4j.open-ai.chat-model.max-tokens=2000
```