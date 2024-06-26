package com.weixin.njuteam.config;

import com.weixin.njuteam.annotation.CurManagerInfo;
import com.weixin.njuteam.annotation.CurUserInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Zyi
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

	/**
	 * 创建API应用
	 * apiInfo() 增加API相关信息
	 * 通过select()函数返回一个ApiSelectorBuilder实例,用来控制哪些接口暴露给Swagger来展现，
	 * 本例采用指定扫描的包路径来定义指定要建立API的目录。
	 *
	 * @return
	 */
	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2)
			.ignoredParameterTypes(CurUserInfo.class)
			.ignoredParameterTypes(CurManagerInfo.class)
			.apiInfo(apiInfo())
			.select()
			.apis(RequestHandlerSelectors.basePackage("com.weixin.njuteam"))
			.paths(PathSelectors.any())
			.build()
			.groupName("NJUTeam")
			.enable(true);
	}

	/**
	 * 创建该API的基本信息（这些基本信息会展现在文档页面中）
	 * 访问地址：http://项目实际地址/swagger-ui.html
	 *
	 * @return
	 */
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
			.title("Weixin Develop")
			.description("微信小程序开发中的接口信息")
			.contact(new Contact("ZY", "https://zyinnju.com", "201250182@smail.nju.edu.cn"))
			.termsOfServiceUrl("https://git.nju.edu.cn/201250182/weixin-mini-project-backend")
			.version("0.1.0")
			.build();
	}
}
