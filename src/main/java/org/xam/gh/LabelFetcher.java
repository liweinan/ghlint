package org.xam.gh;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;


class LabelFetcher {

    static final String GITHUB_TOKEN = GithubToken.get();

    public static void main(String[] args) throws Exception {
        System.out.println("::: " + GITHUB_TOKEN);

        List labels = paginate("https://api.github.com/repos/quarkusio/quarkus/labels");

        System.out.println(labels.size());

    }

    static List paginate(String url) throws Exception {
//        Client client = ClientBuilder.newClient();
        //client.register(EntityLoggingFilter.class);
        ResteasyClient client = new ResteasyClientBuilderImpl().defaultProxy("squid.corp.redhat.com", 3128, "http").build();


        client.register(HTTPLoggingFilter.class);

        WebTarget target = client.target(url);

        Response r = target.request().header("Authorization", "token " + GITHUB_TOKEN).get();

        if(r.getStatus()!=200) {
            throw new IllegalStateException(r.getStatusInfo().toString());
        }

        List data = r.readEntity(List.class);

        Link link = r.getLink("next");
        while (link != null) {
            target = client.target(link.getUri());
            r = target.request().header("Authorization", "token " + GITHUB_TOKEN).get();
            data.addAll(r.readEntity(List.class));
            link = r.getLink("next");
            Thread.sleep(1000);
            //System.out.println(link.getUri());
        }

        return data;
    }
}