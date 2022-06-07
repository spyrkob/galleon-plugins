/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wildfly.galleon.plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.jboss.galleon.universe.maven.MavenArtifact;

public class InstallationMavenCache {

   private final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
   private final Path baseDir;

   public InstallationMavenCache(Path baseDir) {
      this.baseDir = baseDir;
   }

   public void addRecord(MavenArtifact artifact, String path) {
      final Path p = Paths.get(path);
      if(p.isAbsolute()) {
         map.putIfAbsent(toKey(artifact), baseDir.relativize(p).toString());
      } else {
         map.putIfAbsent(toKey(artifact), path);
      }
   }

   public void write() throws IOException {
      final List<String> lines = map.entrySet().stream().map(e -> e.getKey() + ":" + e.getValue()).collect(Collectors.toList());
      Files.write(baseDir.resolve(".installation").resolve("cache.properties"), lines);
   }

   private String toKey(MavenArtifact artifact) {
      return String.format("%s:%s:%s:%s:%s", artifact.getGroupId(), artifact.getArtifactId(), artifact.getClassifier(), artifact.getExtension(), artifact.getVersion());
   }
}
