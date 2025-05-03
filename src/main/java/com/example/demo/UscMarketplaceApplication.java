package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import com.example.demo.servlets.GetConversation;
import com.example.demo.servlets.GetMessage;
import com.example.demo.servlets.SendMessage;

@SpringBootApplication
@ServletComponentScan
public class UscMarketplaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UscMarketplaceApplication.class, args);
	}
	
	/*@Bean
	public ServletRegistrationBean<GetConversation> conversationServlet() {
	    ServletRegistrationBean<GetConversation> bean = new ServletRegistrationBean<>(new GetConversation(), "/get-conversation-servlet");
	    return bean;
	}

	@Bean
	public ServletRegistrationBean<GetMessage> messageServlet() {
	    return new ServletRegistrationBean<>(new GetMessage(), "/get-messages-servlet");
	}

	@Bean
	public ServletRegistrationBean<SendMessage> sendMessageServlet() {
	    return new ServletRegistrationBean<>(new SendMessage(), "/send-message-servlet");
	}*/
}
