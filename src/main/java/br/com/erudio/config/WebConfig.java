package br.com.erudio.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import br.com.erudio.serialization.converter.YamlJackson2HttpMesageConverter;

/**
 * Configurando um conversor de requisição para vários tipos suportados (XML, JSON) e demais outros que podem ser incluídos.
 * */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private static final MediaType MEDIA_TYPE_APPLICATION_YML = MediaType.valueOf("application/x-yaml");

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new YamlJackson2HttpMesageConverter());
	}
	
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		
		/* Via QueryParams */
//		configurer.favorParameter(true) //Aceitando parametros
//			.parameterName("mediaType") //Nome do parâmetro a ser recebido
//			.ignoreAcceptHeader(true) //Ignorando Cabeçalhos
//			.useRegisteredExtensionsOnly(false) //Não usando extensões
//			.defaultContentType(MediaType.APPLICATION_JSON) //Definindo o retorno padrão como JSON
//			.mediaType("json", MediaType.APPLICATION_JSON) //Retorna mediaType em JSON
//			.mediaType("xml", MediaType.APPLICATION_XML); //Retorna mediaType em XML
		
		/* Via Header Params */
		configurer.favorParameter(false) //Não Aceitando parametros
			.ignoreAcceptHeader(false) //Não Ignorando Cabeçalhos
			.useRegisteredExtensionsOnly(false) //Não usando extensões
			.defaultContentType(MediaType.APPLICATION_JSON) //Definindo o retorno padrão como JSON
			.mediaType("json", MediaType.APPLICATION_JSON) //Retorna mediaType em JSON
			.mediaType("xml", MediaType.APPLICATION_XML) //Retorna mediaType em XML
			.mediaType("x-yaml", MEDIA_TYPE_APPLICATION_YML); //Retorna mediaType do Yaml
	}

}
