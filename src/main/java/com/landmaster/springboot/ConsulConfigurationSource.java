package com.landmaster.springboot;

import java.io.StringReader;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Properties;

 import org.apache.commons.lang3.StringUtils;

 import com.google.common.base.Optional;
 import com.netflix.config.PollResult;
 import com.netflix.config.PolledConfigurationSource;
 import com.orbitz.consul.Consul;
 import com.orbitz.consul.KeyValueClient;

 /**
  * 指定archaius读取配置的源头
  */
 public class ConsulConfigurationSource implements PolledConfigurationSource {

     private String keyName;

     public ConsulConfigurationSource(String keyName) {
         this.keyName = keyName;
     }

     /**
      * 默认情况下，每隔60s，该方法会执行一次
      */
     @Override
     public PollResult poll(boolean initial, Object checkPoint) throws Exception {
         Consul consul = Consul.builder().build();
         KeyValueClient kvClient = consul.keyValueClient();

         Optional<String> kvOpt = kvClient.getValueAsString(keyName);

         String kvStr = StringUtils.EMPTY;
         if (kvOpt.isPresent()) {
             kvStr = kvOpt.get();
         }

         Properties props = new Properties();
         props.load(new StringReader(kvStr));//String->Properties

         Map<String, Object> propMap = new HashMap();
         for (Object key : props.keySet()) {
             propMap.put((String) key, props.get(key));
         }
         return PollResult.createFull(propMap);
     }

 }
 /*
 @ApiOperation("get KV from consul by archaius")
 2     @RequestMapping(value="/kv2/",method=RequestMethod.GET)
 3     public void getKVByArchaius(@RequestParam("key") String key) throws IOException {
 4
 5         PolledConfigurationSource source = new ConsulConfigurationSource(key);//定义读取配置的源头
 6         AbstractPollingScheduler scheduler = new FixedDelayPollingScheduler();//设置读取配置文件的
 7         DynamicConfiguration configuration = new DynamicConfiguration(source, scheduler);
 8
 9         ConfigurationManager.install(configuration);
10
11         DynamicStringProperty dsp = DynamicPropertyFactory.getInstance().getStringProperty("mysql.driverClassName", "zhaojigangDriver");
12         System.out.println("当前时间：" + LocalDateTime.now() + "-->值：" + dsp.get());
13         try {
14             Thread.sleep(60000);//睡60s
15         } catch (InterruptedException e) {
16             e.printStackTrace();
17         }
18         System.out.println("当前时间：" + LocalDateTime.now() + "-->值：" + dsp.get());
19     }
  */