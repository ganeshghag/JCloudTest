package com.ghag.rnd.jclouds.client;

import static com.google.common.io.Closeables.closeQuietly;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import com.google.inject.Module;

public class JCloudComputeClientEx {

	private ComputeService compute;
	public static String PRIVATE_KEY= ""; 
	public static String COMMAND= "";

	public static void main(String[] args) {
		JCloudComputeClientEx jCloudsNova = new JCloudComputeClientEx();

		try {
			PRIVATE_KEY=Files.toString(new File("tecgkeypairnew.pem"),Charsets.UTF_8);
			COMMAND=Files.toString(new File("cmd.txt"),Charsets.UTF_8);
			jCloudsNova.init();
			jCloudsNova.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jCloudsNova.close();
		}
	}

	private void init() {
		Iterable<Module> modules = ImmutableSet
				.<Module> of(new SLF4JLoggingModule(),new SshjSshClientModule());
		

		String provider = "openstack-nova";
		String identity = "admin:admin"; // tenantName:userName
		String password = "mastek123"; // demo account uses ADMIN_PASSWORD too

		ComputeServiceContext context = ContextBuilder.newBuilder(provider)
				.endpoint("http://172.16.234.203:5000/v2.0/")
				.credentials(identity, password).modules(modules)
				.buildView(ComputeServiceContext.class);
		compute = context.getComputeService();
		

		
		Set<? extends ComputeMetadata> nodes =  compute.listNodes();
		
		System.out.println("list nodes" + nodes);
		
		for (Iterator iterator = nodes.iterator(); iterator.hasNext();) {
			ComputeMetadata computeMetadata = (ComputeMetadata) iterator.next();
			System.out.println("node info metadata : "+computeMetadata.getName());
			System.out.println("node info metadata : "+computeMetadata.getLocation());
		
			//System.out.println("after getting compute >>>>>>>>>>>>>"+compute.runScriptOnNode(computeMetadata.getId(),null,Builder.over));
			
			ExecResponse resp = compute.runScriptOnNode(computeMetadata.getId(),COMMAND,RunScriptOptions.Builder.overrideLoginUser("ubuntu").overrideLoginPrivateKey(PRIVATE_KEY) );
			System.out.println("resp = "+resp);
			System.out.println("resp = "+resp.getOutput());
			System.out.println("resp = "+resp.getError());
			System.out.println("resp = "+resp.getExitStatus());
			
			//SshClient ssh = context.utils().sshForNode().apply(NodeMetadataBuilder.fromComputeMetadata(computeMetadata));
			//ssh.connect();
			//ssh.

		}

	}

	public void close() {
		closeQuietly(compute.getContext());
	}

}
