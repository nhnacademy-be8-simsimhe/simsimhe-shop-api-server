package com.simsimbookstore.apiserver.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class SwaggerConfig {

        @Bean
        public OpenAPI openAPI() {
            Info info = new Info()
                    .version("v1.0") //버전
                    .title("simsimbook store api") //이름
                    .description("심심북스토어 api 명세서"); //설명
            return new OpenAPI()
                    .info(info);
        }
}
