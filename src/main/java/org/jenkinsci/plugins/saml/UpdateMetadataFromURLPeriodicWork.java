package org.jenkinsci.plugins.saml;

import hudson.Extension;
import hudson.model.PeriodicWork;
import hudson.util.FormValidation.Kind;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

@Extension
public class UpdateMetadataFromURLPeriodicWork extends PeriodicWork {
    private static final Logger LOG = Logger.getLogger(UpdateMetadataFromURLPeriodicWork.class.getName());


    @Override
    public long getRecurrencePeriod() {
        long ret = 30000;
        jenkins.model.Jenkins j = jenkins.model.Jenkins.getInstance();
        if (j.getSecurityRealm() instanceof SamlSecurityRealm) {
            SamlSecurityRealm samlSecurityRealm = (SamlSecurityRealm) j.getSecurityRealm();
            ret = java.util.concurrent.TimeUnit.SECONDS.toMillis(samlSecurityRealm.getIdpMetadataConfiguration().getPeriod());
        }
        return ret;
    }

    @Override
    protected void doRun() throws Exception {
        jenkins.model.Jenkins j = jenkins.model.Jenkins.getInstance();
        if (! (j.getSecurityRealm() instanceof SamlSecurityRealm) ) {
            return;
        }

        SamlSecurityRealm samlSecurityRealm = (SamlSecurityRealm) j.getSecurityRealm();
        String url = samlSecurityRealm.getIdpMetadataConfiguration().getUrl();

        try {
            URLConnection urlConnection = new URL(url).openConnection();
            try (InputStream in = urlConnection.getInputStream()) {
                String xml = IOUtils.toString(in, StringUtils.defaultIfEmpty(urlConnection.getContentEncoding(),"UTF-8"));
                hudson.util.FormValidation validation = new org.jenkinsci.plugins.saml.SamlValidateIdPMetadata(xml).get();
                if(Kind.OK == validation.kind){
                    FileUtils.writeStringToFile(new File(SamlSecurityRealm.getIDPMetadataFilePath()), xml);
                } else {
                    LOG.log(Level.SEVERE, validation.getMessage());
                }
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Was not possible to update the IdP Metadata from the URL " + url , e);
        }
    }
}
