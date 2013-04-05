package com.ghag.rnd.jclouds.client;

import static com.google.common.io.Closeables.closeQuietly;


import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.NovaAsyncApi;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.rest.RestContext;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;


public class JCloudComputeClient {

	private ComputeService compute;
	   private RestContext<NovaApi, NovaAsyncApi> nova;
	   private Set<String> zones;

	   public static void main(String[] args) {
		   JCloudComputeClient jCloudsNova = new JCloudComputeClient();

	      try {
	         jCloudsNova.init();
	         jCloudsNova.listServers();
	         jCloudsNova.close();
	      }
	      catch (Exception e) {
	         e.printStackTrace();
	      }
	      finally {
	         jCloudsNova.close();
	      }
	   }
	   
	   private void init() {
		      Iterable<Module> modules = ImmutableSet.<Module> of(new SLF4JLoggingModule());

		      String provider = "openstack-nova";
		      String identity = "demo:admin"; // tenantName:userName
		      String password = "mastek123"; // demo account uses ADMIN_PASSWORD too

		      ComputeServiceContext context = ContextBuilder.newBuilder(provider)
		            .endpoint("http://172.16.234.203:5000/v2.0/")
		            .credentials(identity, password)
		            .modules(modules)
		            .buildView(ComputeServiceContext.class);
		      compute = context.getComputeService();
		      nova = context.unwrap();
		      zones = nova.getApi().getConfiguredZones();
		      
		   }

		   private void listServers() {
		      for (String zone: zones) {
		         ServerApi serverApi = nova.getApi().getServerApiForZone(zone);

		         System.out.println("Servers in " + zone);

		         for (Server server: serverApi.listInDetail().concat()) {
		            System.out.println("Server Info:  " + server);
		         }
		      }
		   }

		   public void close() {
		      closeQuietly(compute.getContext());
		   }
	
}
