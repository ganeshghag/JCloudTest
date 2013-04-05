package com.ghag.rnd.jclouds.client;

import static com.google.common.io.Closeables.closeQuietly;

import java.io.Closeable;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.swift.CommonSwiftAsyncClient;
import org.jclouds.openstack.swift.CommonSwiftClient;
import org.jclouds.openstack.swift.domain.ContainerMetadata;
import org.jclouds.rest.RestContext;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

public class JCloudSwift implements Closeable {
   private BlobStore storage;
   private RestContext<CommonSwiftClient, CommonSwiftAsyncClient> swift;

   public static void main(String[] args) {
      JCloudSwift jCloudsSwift = new JCloudSwift();

      try {
         jCloudsSwift.init();
         jCloudsSwift.listContainers();
         jCloudsSwift.close();
      }
      catch (Exception e) {
         e.printStackTrace();
      }
      finally {
         jCloudsSwift.close();
      }
   }
   
   private void init() {
      Iterable<Module> modules = ImmutableSet.<Module> of(
            new SLF4JLoggingModule());

      String provider = "swift-keystone";
      String identity = "demo:admin"; // tenantName:userName
      String password = "mastek123"; // demo account uses ADMIN_PASSWORD too

      BlobStoreContext context = ContextBuilder.newBuilder(provider)
            .endpoint("http://172.16.234.203:5000/v2.0/")
            .credentials(identity, password)
            .modules(modules)
            .buildView(BlobStoreContext.class);
      storage = context.getBlobStore();
      swift = context.unwrap();
   }

   private void listContainers() {
      System.out.println("List Containers");
      Set<ContainerMetadata> containers = swift.getApi().listContainers();

      for (ContainerMetadata container: containers) {
         System.out.println("Container Info:  " + container);
      }
   }

   public void close() {
      closeQuietly(storage.getContext());
   }
}
