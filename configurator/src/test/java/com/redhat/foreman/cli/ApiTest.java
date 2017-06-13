package com.redhat.foreman.cli;

import com.redhat.foreman.cli.exception.ForemanApiException;
import com.redhat.foreman.cli.model.Architecture;
import com.redhat.foreman.cli.model.Domain;
import com.redhat.foreman.cli.model.Environment;
import com.redhat.foreman.cli.model.Host;
import com.redhat.foreman.cli.model.Hostgroup;
import com.redhat.foreman.cli.model.Medium;
import com.redhat.foreman.cli.model.OperatingSystem;
import com.redhat.foreman.cli.model.PTable;
import com.redhat.foreman.cli.model.Parameter;
import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * Created by shebert on 17/01/17.
 */
public class ApiTest extends AbstractTest {
    private static Logger LOGGER = Logger.getLogger(ApiTest.class);

    @Test
    public void testCompleteCreate() throws ForemanApiException {
        String url = getUrl();

        Api api = new Api(url, user, password);

        Domain domain = api.createDomain("scoheb.com");
        Environment environment = api.createEnvironment("staging");
        Architecture architecture = api.getArchitecture("x86_64");
        PTable ptable = api.getPTable("Kickstart default");
        Medium medium = api.getMedium("Fedora mirror");
        OperatingSystem os = api.createOperatingSystem("RedHat", "7", "7", architecture.id,
                medium.id, ptable.id, "Redhat");
        Hostgroup hostGroup = api.createHostGroup("staging servers", environment.id,
                domain.id, architecture.id, os.id,
                medium.id, ptable.id, "changeme");

        Host host = api.createHost("stage1", "127.0.0.1",
                domain, hostGroup.id,
                architecture.id, os.id, medium.id, ptable.id, environment.id,
                "changeme",
                "50:7b:9d:4d:f1:12");

        LOGGER.info(domain);
        LOGGER.info(environment);
        LOGGER.info(architecture);
        LOGGER.info(medium);
        LOGGER.info(ptable);
        LOGGER.info(os);
        LOGGER.info(hostGroup);
        LOGGER.info(host);

        Parameter reservedParam = new Parameter("RESERVED", "false");
        Parameter remoteFSParam = new Parameter("JENKINS_SLAVE_REMOTEFS_ROOT", "/tmp/remoteFSRoot");
        Parameter labelParam = new Parameter("JENKINS_LABEL", "example1");

        api.updateHostParameter(host, remoteFSParam);

        host = api.addHostParameter(host, reservedParam);
        host = api.addHostParameter(host, labelParam);

        Parameter reservedTrueParam = new Parameter("RESERVED", "true");
        api.updateHostParameter(host, reservedTrueParam);
        host = api.getHost(host.getName());
        LOGGER.info(host.getParameterValue("RESERVED").getValue());
        api.updateHostParameter(host, reservedParam);
        host = api.getHost(host.getName());
        LOGGER.info(host.getParameterValue("RESERVED").getValue());
        api.updateHostParameter(host, reservedTrueParam);
        host = api.getHost(host.getName());
        LOGGER.info(host.getParameterValue("RESERVED").getValue());
    }
}
