package com.m3958.vertxio.assetfeed.integration.java;

/*
 * Copyright 2013 Red Hat, Inc.
 * 
 * Red Hat licenses this file to you under the Apache License, version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @author <a href="http://tfox.org">Tim Fox</a>
 */

import static org.vertx.testtools.VertxAssert.assertNotNull;
import static org.vertx.testtools.VertxAssert.assertTrue;
import static org.vertx.testtools.VertxAssert.testComplete;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.testtools.TestVerticle;

import com.google.common.io.Files;

/**
 * Example Java integration test that deploys the module that this project
 * builds.
 * 
 * Quite often in integration tests you want to deploy the same module for all
 * tests and you don't want tests to start before the module has been deployed.
 * 
 * This test demonstrates how to do that.
 */
public class FileUploadTest extends TestVerticle {

	@Test
	public void testPostRename() throws ClientProtocolException, IOException,
			URISyntaxException {

		File f = new File("README.md");
		
		InputStream is = Files.asByteSource(f).openBufferedStream();

		Assert.assertTrue(f.exists());

		String c = Request
				.Post(new URIBuilder().setScheme("http").setHost("localhost")
						.setPort(8080).build())
				.body(MultipartEntityBuilder
						.create()
						.addBinaryBody("afile", is,
								ContentType.MULTIPART_FORM_DATA, f.getName())
						.addTextBody("fn", "abcfn.md").build()).execute()
				.returnContent().asString();

		String url = c.trim();
		Assert.assertTrue(url.endsWith("abcfn.md"));

		testComplete();
	}

	@Override
	public void start() {
		// Make sure we call initialize() - this sets up the assert stuff so
		// assert functionality works
		// correctly
		initialize();
		// Deploy the module - the System property `vertx.modulename` will
		// contain the name of the
		// module so you
		// don't have to hardecode it in your tests
		container.logger().info(System.getProperty("vertx.modulename"));
		container.deployModule(System.getProperty("vertx.modulename"),
				new AsyncResultHandler<String>() {
					@Override
					public void handle(AsyncResult<String> asyncResult) {
						// Deployment is asynchronous and this this handler will
						// be called when it's complete
						// (or failed)
						assertTrue(asyncResult.succeeded());
						assertNotNull("deploymentID should not be null",
								asyncResult.result());
						// If deployed correctly then start the tests!
						startTests();
					}
				});
	}

}
