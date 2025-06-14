# demo-springboot-250613
包含 Redis OpenAPI 的範例

## 說明

- Java 21, SpringBoot 3.5.0, Gradle 不用裝 使用 gradlew 就好.
- 如果有要用到 googld cloud sdk 要暫時先降到 Springboot 3.4.6, GCP 相關套件現在還在 3.5.0-M1.

## 設定檔

- src/main/resources/application.yml
  將會打包到 docker image 中, 所以不要有機密資訊.

- config/application-local.yml
  這個是 local (本機開發)的設定檔, 不會打包到 docker image 中, 所以可以有機密資訊.

## 工具說明

openapi.yaml 有更動時需手動整理重新編譯 `./gradlew clean openApiGenerate`  


## VSCode 設定

### 建立 launch.json

可以手動增加 `.vscode/launch.json` , 圖形化介面可從左邊的 Debug 選單新增配置，如下：  
作用是在透過 IDE 啟動 springboot 時可以指定 profile, 這樣就可以在 local 環境中使用 local 的設定檔.
``` json
{
    "version": "0.2.0",
    "configurations": [

        {
            "type": "java",
            "name": "Current File",
            "request": "launch",
            "mainClass": "${file}"
        },
        {
            "type": "java",
            "name": "DemoApplication",
            "request": "launch",
            "mainClass": "com.example.demo.DemoApplication",
            "projectName": "demo-springboot-250613",
            "env": {
                "spring.profiles.active": "local-env,local"
            }
        },
        {
            "type": "java",
            "name": "TestDemoApplication",
            "request": "launch",
            "mainClass": "com.example.demo.TestDemoApplication",
            "projectName": "demo-springboot-250613"
        }
    ]
}
```

## 從頭開始的話

[下載專案模板](https://start.spring.io/#!type=gradle-project&language=java&platformVersion=3.5.0&packaging=jar&jvmVersion=21&groupId=com.example&artifactId=demo&name=demo&description=Demo%20project%20for%20Spring%20Boot&packageName=com.example.demo&dependencies=lombok,devtools,configuration-processor,docker-compose,web,data-jpa,oauth2-resource-server,liquibase,postgresql,data-redis,validation,cache,actuator,sbom-cyclone-dx,otlp-metrics,testcontainers,distributed-tracing,prometheus)


