package com.cis.regularPeer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import rmi.common.Interface.BlockchainInterface;

@Configuration
public class RmiClientConfig {
	
	@Value("${super.peer.address}")
    private String superPeer;

    @Bean
    public RmiProxyFactoryBean rmiProxyFactoryBean() {
        RmiProxyFactoryBean factoryBean = new RmiProxyFactoryBean();
        factoryBean.setServiceUrl(superPeer);
        factoryBean.setServiceInterface(BlockchainInterface.class);
        return factoryBean;
    }
}
