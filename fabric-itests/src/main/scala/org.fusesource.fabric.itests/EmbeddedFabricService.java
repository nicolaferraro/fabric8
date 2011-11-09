package org.fusesource.fabric.itests;

import de.kalpatec.pojosr.framework.launch.BundleDescriptor;
import de.kalpatec.pojosr.framework.launch.ClasspathScanner;
import de.kalpatec.pojosr.framework.launch.PojoServiceRegistry;
import de.kalpatec.pojosr.framework.launch.PojoServiceRegistryFactory;
import org.fusesource.fabric.api.FabricService;
import org.fusesource.fabric.api.ZooKeeperClusterService;
import org.osgi.framework.*;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class EmbeddedFabricService {
    private static final Logger log = LoggerFactory.getLogger("Test Registry");
    private PojoServiceRegistry registry = null;

    public void start() throws Exception {
        System.setProperty("org.osgi.framework.storage", "target/osgi/" + System.currentTimeMillis());
        System.setProperty("karaf.name", "root");

        List<BundleDescriptor> bundles = new ClasspathScanner().scanForBundles();

        log.info("Located following bundles on classpath : ");
        for ( BundleDescriptor desc : bundles ) {
            log.debug("Bundle : {}", desc);
        }

        Map config = new HashMap();
        config.put(PojoServiceRegistryFactory.BUNDLE_DESCRIPTORS, bundles);
        ServiceLoader<PojoServiceRegistryFactory> loader = ServiceLoader.load(PojoServiceRegistryFactory.class);
        registry = loader.iterator().next().newPojoServiceRegistry(config);
        registry.addServiceListener(new ServiceListener() {
            @Override
            public void serviceChanged(ServiceEvent event) {
                ServiceReference ref = event.getServiceReference();
                Object service = registry.getService(ref);
                switch (event.getType()) {
                    case ServiceEvent.MODIFIED:
                        log.info("Service modified : " + service);
                        break;
                    case ServiceEvent.MODIFIED_ENDMATCH:
                        log.info("Service modified endmatch : " + service);
                        break;
                    case ServiceEvent.REGISTERED:
                        log.info("Service registering : " + service);
                        break;
                    case ServiceEvent.UNREGISTERING:
                        log.info("Service unregistering : " + service);
                        break;
                    default:
                        log.info("Unknown event : " + event.getType() + " service : " + service);
                }
            }
        });
        Thread.sleep(10000);

        dumpBundles();
        dumpServiceReferences();
        createZooKeeperCluster();

    }

    public void dumpBundles() {
        BundleContext bc = registry.getBundleContext();
        for (Bundle b : bc.getBundles()) {
            switch (b.getState()) {
                case Bundle.ACTIVE:
                    log.debug(String.format("Found bundle : %s with version %s in state ACTIVE", b.getSymbolicName(), b.getVersion()));
                    break;
                case Bundle.INSTALLED:
                    log.debug(String.format("Found bundle : %s with version %s in state INSTALLED", b.getSymbolicName(), b.getVersion()));
                    break;
                case Bundle.RESOLVED:
                    log.debug(String.format("Found bundle : %s with version %s in state RESOLVED", b.getSymbolicName(), b.getVersion()));
                    break;
                case Bundle.STARTING:
                    log.debug(String.format("Found bundle : %s with version %s in state STARTING", b.getSymbolicName(), b.getVersion()));
                    break;
                case Bundle.STOPPING:
                    log.debug(String.format("Found bundle : %s with version %s in state STOPPING", b.getSymbolicName(), b.getVersion()));
                    break;
                case Bundle.UNINSTALLED:
                    log.debug(String.format("Found bundle : %s with version %s in state UNINSTALLED", b.getSymbolicName(), b.getVersion()));
                    break;
                default:
                    log.debug(String.format("Found bundle : %s with version %s in state %s", b.getSymbolicName(), b.getVersion(), b.getState()));
                    break;
            }
        }
    }

    public void dumpServiceReferences() throws Exception {
        BundleContext bc = registry.getBundleContext();
        for (ServiceReference ref : bc.getAllServiceReferences(null, null)) {
            log.debug(String.format("Found Service reference : %s", ref.toString()));
        }
    }

    public void stop() {
        BundleContext bc = registry.getBundleContext();

        for (Bundle b : bc.getBundles()) {
            log.debug(String.format("Stopping bundle : %s", b.getSymbolicName()));
            try {
                b.stop();
            } catch (BundleException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        registry = null;
    }

    public PojoServiceRegistry getRegistry() {
        return registry;
    }

    public static <T> T getService(Class<T> type, String filter, PojoServiceRegistry registry) {
        try {
            ServiceReference[] ref = registry.getServiceReferences(type.getName(), filter);
            if ( ref == null ) {
                return null;
            }
            if ( ref.length > 1 || ref.length == 0 ) {
                return null;
            }
            return type.cast(registry.getService(ref[0]));
        } catch (InvalidSyntaxException e) {
            return null;
        }
    }

    public static <T> T getService(Class<T> type, PojoServiceRegistry registry) {
        ServiceReference ref = registry.getServiceReference(type.getName());
        if ( ref == null ) {
            return null;
        }
        return type.cast(registry.getService(ref));
    }

    public ConfigurationAdmin getConfigAdmin() {
        return getService(ConfigurationAdmin.class, registry);
    }

    public ZooKeeperClusterService getZooKeeperClusterService() throws Exception {
        return getService(ZooKeeperClusterService.class, getRegistry());
    }

    public void createZooKeeperCluster() throws Exception {
        ZooKeeperClusterService clusterService = getZooKeeperClusterService();

        List<String> agents = new ArrayList<String>();
        agents.add("root");
        clusterService.createCluster(agents);

        Thread.sleep(10000);
    }

    public FabricService getFabricService() throws Exception {
        FabricService service = getService(FabricService.class, getRegistry());
        Thread.sleep(10000);
        return service;
    }
}