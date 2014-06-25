package com.m3958.vertxio.assetfeed;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VoidHandler;
import org.vertx.java.core.file.FileSystem;
import org.vertx.java.core.http.HttpServerFileUpload;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.core.logging.Logger;

import com.google.common.collect.Lists;

public class PostFileHandler implements Handler<HttpServerRequest> {

  private Logger logger;

  private FileSystem fileSystem;

  private String assetRoot;

  public PostFileHandler(Vertx vertx, Logger logger, String assetRoot) {
    this.logger = logger;
    this.fileSystem = vertx.fileSystem();
    this.assetRoot = assetRoot;
  }

  /**
   * curl --form upload=@localfilename --form press=OK localhost allow upload one file per form.
   */
  @Override
  public void handle(final HttpServerRequest request) {
    request.expectMultiPart(true);
    final HttpServerResponse response = request.response();
    final String host = request.headers().get("Host");
    final List<String> upfiles = Lists.newArrayList();
    request.uploadHandler(new Handler<HttpServerFileUpload>() {
      public void handle(HttpServerFileUpload upload) {
        if (upfiles.size() == 0) {
          String rs = Paths.get(upload.filename()).getFileName().toString();
          Path fpath = Paths.get(assetRoot, rs);
          upload.streamToFileSystem(fpath.toString());
          upfiles.add(rs);
        }
      }
    });

    request.endHandler(new VoidHandler() {
      public void handle() {
        String restr;
        if (upfiles.size() > 0) {
          MultiMap attrs = request.formAttributes();

          String randomfn = upfiles.get(0);

          String fn = attrs.get("fn");

          if (fn == null || fn.length() < 3) {
            fn = randomfn;
          } else {
            String randomPath = Paths.get(assetRoot, randomfn).toString();
            String fnPath = Paths.get(assetRoot, fn).toString();
            if (fileSystem.existsSync(fnPath)) {
              fileSystem.deleteSync(fnPath);
            }
            fileSystem.moveSync(randomPath, fnPath);
          }
          restr = "http://" + host + "/" + fn;
        } else {
          restr = "1";
        }
        response.putHeader("content-type", "text/plain");
        response.end(restr);
      }
    });
  }
}
