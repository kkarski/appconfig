package com.appcrossings.config.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.yaml.snakeyaml.Yaml;

public class YamlProcessor {

  public static boolean isYamlFile(String path) {

    assert StringUtils.hasText(path) : "Path was null or empty";
    return (path.toLowerCase().endsWith(".yaml") || path.toLowerCase().endsWith(".yml"));
  }

  public static Properties asProperties(InputStream stream) {

    Yaml yaml = new Yaml();
    final Properties properties = new Properties();
    LinkedHashMap<String, Object> map = (LinkedHashMap) yaml.load(stream);

    StringBuilder builder = new StringBuilder();
    recurse(map, builder, properties);

    return properties;
  }

  private static void recurse(List<Object> list, StringBuilder builder, Properties props) {

    final String node = builder.toString();

    int i = 0;
    for (Object k : list) {

      if (k instanceof String) {

        String key = builder.toString() + "[" + i + "]";
        props.put(key, k);

      } else if (k instanceof LinkedHashMap) {

        recurse((Map) k, builder, props);

      } else if (k instanceof ArrayList) {

        recurse((List) k, builder, props);

      }

      builder = new StringBuilder(node);
      i++;
    }

  }

  private static void recurse(Map<String, Object> map, StringBuilder builder, Properties props) {

    final String node = builder.toString();


    for (Object k : map.keySet()) {

      Object i = map.get(k);

      if (builder.length() > 0)
        builder.append("." + k);
      else
        builder.append(k);

      if (i instanceof LinkedHashMap) {

        recurse((Map) i, builder, props);

      } else if (i instanceof ArrayList) {

        recurse((List) i, builder, props);

      } else if (i instanceof String) {

        props.put(builder.toString(), i);

      }

      builder = new StringBuilder(node);
    }
  }


}