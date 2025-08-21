package com.ezen.shop.kakaopay;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/*


@Component
@ConfigurationProperties(prefix = "kakaopay")
*/
@ToString
@Getter
@Setter
@Configuration
@PropertySource("classpath:kakaopay/kakaopay.properties")
public class KakaoPayProperties {

	@Value("${kakaopay.secretKey}")
	private String secretKey;
	@Value("${kakaopay.cid}")
	private String cid;
}
