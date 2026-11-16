module workplan.ai {

    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires jakarta.annotation;
    requires spring.context;
    requires spring.ai.model;
    requires spring.ai.ollama;
    requires spring.ai.client.chat;
    requires static lombok;
    requires org.jspecify;
    requires spring.web;
    requires com.google.gson;
    requires client;
    requires spring.beans;
    requires spring.core;
    requires com.google.common;
    requires spring.ai.autoconfigure.vector.store.qdrant;
    requires org.apache.commons.lang3;
    requires com.baomidou.mybatis.plus.annotation;
    requires fesod.sheet;
    requires org.mybatis;
    requires com.baomidou.mybatis.plus.core;
    requires org.mybatis.spring;


}