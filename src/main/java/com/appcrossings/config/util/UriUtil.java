package com.appcrossings.config.util;

import java.io.File;
import java.net.URI;
import java.net.URL;

public class UriUtil {

  private boolean isURL = false;
  private final URI uri;
  private String manipulated;
  private URL url;
  private URI host;
  private String fileName;
  private boolean isClasspath = false;

  public UriUtil(String uri) {
    this.uri = URI.create(uri);

    if (uri.toLowerCase().startsWith("classpath"))
      this.isClasspath = true;

    if (StringUtils.hasText(this.uri.getPath()))
      this.manipulated = this.uri.getPath();
    else
      this.manipulated = this.uri.getSchemeSpecificPart();

    if (hasFile()) {
      fileName =
          this.manipulated.substring(this.manipulated.lastIndexOf("/"), this.manipulated.length());
      this.manipulated = this.manipulated.replace(fileName, "");
    }

    try {
      this.url = this.uri.toURL();
      this.isURL = true;
    } catch (Exception e) {
      this.url = null;
    }
  }

  public URI getHost() {
    if (isURL && this.host == null) {
      this.host =
          URI.create(this.uri.getScheme() + "://" + this.uri.getHost() + this.uri.getPort());
    }

    return host;
  }

  public boolean hasPath() {

    if (this.manipulated.startsWith("/"))
      return this.manipulated.lastIndexOf("/") > -1;

    return this.manipulated.contains("/");
  }

  public boolean hasFile() {
    return this.uri.toString().substring(this.uri.toString().lastIndexOf("/")).contains(".");
  }

  public boolean isURL() {
    return this.isURL;
  }

  public URL toURL() {
    if (isURL)
      try {
        return URI.create(toString()).toURL();
      } catch (Exception e) {
        // should never happen
      }

    return null;
  }

  public void stripPath() {

    int i = this.manipulated.lastIndexOf("/");

    if (hasPath())
      this.manipulated = this.manipulated.substring(0, i);

  }

  public boolean isConfigrdServer() {

    String basePath;
    if (StringUtils.hasText(this.uri.getPath()))
      basePath = this.uri.getPath();
    else
      basePath = this.uri.getSchemeSpecificPart();

    return basePath.toLowerCase().contains("configrd/v1");
  }

  public String getScheme() {
    return uri.getScheme();
  }

  public String getSchemeSpecificPart() {
    return uri.getSchemeSpecificPart();
  }

  public String firstPathSegment() {

    String[] paths = uri.getSchemeSpecificPart().split(File.separator);
    String firstPath = uri.getPath();
    String repoName = uri.getScheme();

    if (paths.length > 0) {
      for (String p : paths) {
        if (StringUtils.hasText(p)) {
          firstPath = p;
          break;
        }
      }
    }

    return firstPath;
  }

  public void appendFileName(String fileName) {

    if (StringUtils.hasText(fileName) && !hasFile())
      this.fileName = File.separator + fileName.trim();

  }

  public void replaceFileName(String fileName) {
    if (StringUtils.hasText(fileName))
      this.fileName = File.separator + fileName.trim();
  }

  @Override
  public String toString() {

    StringBuilder s = new StringBuilder();

    if (isURL) {
      s.append(this.uri.getScheme() + ":");

      if (StringUtils.hasText(this.uri.getAuthority())) {
        s.append("//" + this.uri.getAuthority());

        if (this.uri.getPort() > 0)
          s.append(":" + this.uri.getPort());
      }

    } else if (isClasspath) {
      s.append(this.uri.toString().substring(0, this.uri.toString().indexOf(":")) + ":");
    }

    s.append(this.manipulated);

    if (StringUtils.hasText(this.fileName)) {
      if (this.fileName.startsWith(File.separator) && this.manipulated.endsWith(File.separator)) {
        s.append(this.fileName.replaceAll(File.separator, ""));
      } else {
        s.append(this.fileName);
      }
    }

    return s.toString();
  }

}