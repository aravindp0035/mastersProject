package com.cis.superPeer1;

import rmi.common.Interface.BlockchainInterface;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;


@Configuration
public class RmiClientConfig {

	@Value("${super.peer.addresses}")
    private String superPeerAddress;

    @Value("${initial.super.peer.address}")
    private String initialSuperPeerAddress;
    
   

    @Bean
    @Primary
    public RmiProxyFactoryBean initialSuperPeerFactoryBean() {
        RmiProxyFactoryBean factoryBean = new RmiProxyFactoryBean();
        factoryBean.setServiceUrl(initialSuperPeerAddress);
        factoryBean.setServiceInterface(BlockchainInterface.class);
        return factoryBean;
    }

    @Bean
    public RmiProxyFactoryBean superPeerFactoryBean() {
        RmiProxyFactoryBean factoryBean = new RmiProxyFactoryBean();
        factoryBean.setServiceUrl(superPeerAddress);
        factoryBean.setServiceInterface(BlockchainInterface.class);
        return factoryBean;
    }


}


