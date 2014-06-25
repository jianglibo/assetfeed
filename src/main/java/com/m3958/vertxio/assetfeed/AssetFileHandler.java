package com.m3958.vertxio.assetfeed;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.vertx.java.core.Vertx;
import org.vertx.java.core.file.FileSystem;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.logging.Logger;
import org.vertx.mods.web.StaticFileHandler;

public class AssetFileHandler extends StaticFileHandler {

  private Logger logger;

  private String assetRoot;
  
  private FileSystem fileSystem;

  public AssetFileHandler(Vertx vertx, Logger logger, String assetRoot, boolean gzipFiles,
      boolean caching) {
    super(vertx, assetRoot, gzipFiles, caching);
    this.logger = logger;
    this.assetRoot = assetRoot;
    this.fileSystem = vertx.fileSystem();
  }

  @Override
  public void handle(HttpServerRequest req) {
    Path p = Paths.get(assetRoot, req.path().substring(1));
    if (fileSystem.existsSync(p.toString())) {
      super.handle(req);
    } else {
      req.response().end("File Not Found!");
    }
  }
}
