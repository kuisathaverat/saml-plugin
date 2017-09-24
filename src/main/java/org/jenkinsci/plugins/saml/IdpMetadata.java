package org.jenkinsci.plugins.saml;

import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;

public class IdpMetadata{
    private String xml;
    private String source;
    private String url;
    private Long period;

    @DataBoundConstructor
    public IdpMetadata(String source, String xml, String url, Long period) {
        this.xml = xml;
        this.source = source;
        this.url = url;
        this.period = period;
    }

    public IdpMetadata(@Nonnull String xml) {
        this.xml = xml;
        this.period = 0L;
    }

    public IdpMetadata(@Nonnull String url, @Nonnull Long period) throws MalformedURLException {
        this.url = url;
        this.period = period;
    }

    public String getXml() {
        return xml;
    }

    public String getSource() {
        return source;
    }

    public String getUrl() {
        return url;
    }

    public Long getPeriod() {
        return period;
    }

    public boolean isInline(){
        return xml != null;
    }
}
