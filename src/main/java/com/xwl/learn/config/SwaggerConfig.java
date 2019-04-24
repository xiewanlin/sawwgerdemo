package com.xwl.learn.config;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
public class SwaggerConfig {

  @Bean
  public Docket createInnerApi() {
    return new Docket(DocumentationType.SWAGGER_2)
    	.groupName("xwl内部接口文档-inner")
        .useDefaultResponseMessages(false)
        .enable(true)
        .apiInfo(apiInnerInfo())
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.xwl.learn.controller.inner"))
        .paths(PathSelectors.any())
        .build() ;
  }

  private ApiInfo apiInnerInfo() {
    return new ApiInfoBuilder()
        .title("xwl内部接口文档")
        .version("1.0.0")
        .build();
  }

  @Bean
  public Docket createAdminApi() {
    return new Docket(DocumentationType.SWAGGER_2)
            .groupName("xwl管理中心")
            .useDefaultResponseMessages(false)
            .enable(true)
            .apiInfo(apiAdminInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.xwl.learn.controller.admin"))
            .paths(PathSelectors.any())
            .build() ;
  }

  private ApiInfo apiAdminInfo() {
    return new ApiInfoBuilder()
            .title("管理中心")
            .version("1.0.0")
            .build();
  }

//  @Bean
//  CustomModelBuildPlugin customModelBuildPlugin(){
//    return new CustomModelBuildPlugin();
//  }

}
