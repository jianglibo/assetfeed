package com.m3958.vertxio.assetfeed;

/*
 * 
 * @author <a href="http://tfox.org">Tim Fox</a>
 */

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.mods.web.WebServerBase;

public class AssetFeedServer extends WebServerBase {

  // Multiple matches can be specified for each HTTP verb. In the case there
  // are more than one matching patterns for a particular request, the first
  // matching one will be used.
  @Override
  protected RouteMatcher routeMatcher() {
    RouteMatcher matcher = new RouteMatcher();
    matcher.getWithRegEx("/.+", getAssetFileHandler());
    matcher.post("/", postFileHandler());
    System.out.println(System.getProperty("user.dir"));
    return matcher;
  }


  private Handler<HttpServerRequest> postFileHandler() {
    return new PostFileHandler(vertx, container.logger(), config.getString("asset_root", "asset/"));
  }


  private Handler<HttpServerRequest> getAssetFileHandler() {
    String assetRoot = config.getString("asset_root", "asset/");
    return new AssetFileHandler(vertx, container.logger(), assetRoot, false, false);
  }
}
