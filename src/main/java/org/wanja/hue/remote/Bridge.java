package org.wanja.hue.remote;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Singleton;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.RestClientDefinitionException;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Bridge extends PanacheEntity {
    public String bridgeNumber;
    public String name;

    public String baseURL;
    public String authToken;

}
