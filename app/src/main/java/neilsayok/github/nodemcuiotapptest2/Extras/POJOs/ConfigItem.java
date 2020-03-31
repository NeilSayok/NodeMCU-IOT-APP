package neilsayok.github.nodemcuiotapptest2.Extras.POJOs;

import android.util.Log;

import java.net.URI;

public class ConfigItem {
    private String name, host,ip;
    private URI uri;
    private int port;



    public ConfigItem(String name, String host,  URI uri) {
        this.name = name;
        this.host = host;
        this.uri = uri;

        Log.d(name, uri.toString());
    }

    public ConfigItem() {
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }
}
