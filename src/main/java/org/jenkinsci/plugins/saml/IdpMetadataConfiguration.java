package org.jenkinsci.plugins.saml;

import hudson.util.FormValidation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

/**
 * Class to store the info about how to manage the IdP Metadata.
 */
public class IdpMetadataConfiguration {
    /**
     * IdP Metadata on XML format, it implies there is not automatic updates.
     */
    private String xml;

    /**
     * URL to update the IdP Metadata from.
     */
    private String url;
    /**
     * Period in minutes between each IdP Metadata update.
     */
    private Long period;

    /**
     * Jelly Constructor.
     * @param xml Idp Metadata XML. if xml is null, url and period should not.
     * @param url Url to download the IdP Metadata.
     * @param period Period in minutes between updates of the IdP Metadata.
     */
    @DataBoundConstructor
    public IdpMetadataConfiguration(String xml, String url, Long period) {
        this.xml = xml;
        this.url = url;
        this.period = period;
    }

    /**
     * Inline Constructor.
     * @param xml IdP Metadata XML.
     */
    public IdpMetadataConfiguration(@Nonnull String xml) {
        this.xml = xml;
        this.period = 0L;
    }

    /**
     * Idp Metadata downloaded from an Url Constructor.
     * @param url URL to grab the IdP Metadata.
     * @param period Period between updates of the IdP Metadata.
     */
    public IdpMetadataConfiguration(@Nonnull String url, @Nonnull Long period) {
        this.url = url;
        this.period = period;
    }

    public String getXml() {
        return xml;
    }

    public String getUrl() {
        return url;
    }

    public Long getPeriod() {
        return period;
    }

    public boolean isInline() {
        return xml != null;
    }

    /**
     * @return Return the Idp Metadata from the XML file JENKINS_HOME/saml-idp.metadata.xml.
     * @throws IOException in case it can not read the IdP Metadata file.
     */
    public String getIdpMetadata() throws IOException {
        return FileUtils.readFileToString(new File(SamlSecurityRealm.getIDPMetadataFilePath()));
    }

    /**
     * Creates the IdP Metadata file (saml-idp.metadata.xml) in JENKINS_HOME using the configuration.
     * @throws IOException in case of error writing the file.
     */
    public void createIdPMetadataFile() throws IOException {
        try {
            if (isInline()) {
                FileUtils.writeStringToFile(new File(SamlSecurityRealm.getIDPMetadataFilePath()), xml);
            } else {
                updateIdPMetadata();
            }
        } catch (IOException e) {
            throw new IOException("Can not write IdP metadata file in JENKINS_HOME", e);
        }
    }

    /**
     * Gets the IdP Metadata from an URL, then validate it and write it to a file (JENKINS_HOME/saml-idp.metadata.xml).
     * @throws IOException in case of error writing the file or validating the content.
     */
    public void updateIdPMetadata() throws IOException {
        try {
            URLConnection urlConnection = new URL(url).openConnection();
            try (InputStream in = urlConnection.getInputStream()) {
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer transformer = tf.newTransformer();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                StringWriter writer = new StringWriter();
                transformer.transform(new StreamSource(in), new StreamResult(writer));
                String xml = writer.toString();
                FormValidation validation = new SamlValidateIdPMetadata(xml).get();
                if (FormValidation.Kind.OK == validation.kind) {
                    FileUtils.writeStringToFile(new File(SamlSecurityRealm.getIDPMetadataFilePath()), xml);
                } else {
                    throw new IllegalArgumentException(validation.getMessage());
                }
            }
        } catch (IOException | TransformerException e) {
            throw new IOException("Was not possible to update the IdP Metadata from the URL " + url, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IdpMetadataConfiguration{");
        sb.append("xml='").append(xml).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", period=").append(period);
        sb.append('}');
        return sb.toString();
    }
}
