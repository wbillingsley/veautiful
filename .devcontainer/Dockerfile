FROM bellsoft/liberica-openjdk-alpine:11

RUN apk add --no-cache bash curl git zip npm 

# User the Java-based launcher as Alpine Linux and Graal native compiled images of CS don't like
# Alpine Linux (due to musl instead of gcc)
RUN bash -c 'curl -fLo /usr/bin/cs https://git.io/coursier-cli' && \
    chmod u+x /usr/bin/cs

# Put Coursier-installed artifacts on the path
ENV PATH="$PATH:/root/.local/share/coursier/bin"   

# Get sbt
RUN bash -c 'cs install sbt'

# Get http server
RUN bash -c 'npm install -g http-server'

# Precache sbt version as part of the image.
ARG SBT_VERSION=1.5.5
ARG SCALA_VERSION=3.1.0
ARG SCALAJS_VERSION=1.7.1
RUN mkdir -p /sbtprecacheproj/project && \
  cd /sbtprecacheproj && \
  echo sbt.version=${SBT_VERSION} > project/build.properties && \
  echo 'addSbtPlugin("org.scala-js" % "sbt-scalajs" % "'${SCALAJS_VERSION}'")' > project/plugins.sbt && \
  echo '@main def hello = println("hello")' > main.scala && \
  echo 'enablePlugins(ScalaJSPlugin); scalaVersion := "'${SCALA_VERSION}'"' > build.sbt && \
  sbt fastLinkJS && cd / && rm -r -f /tmp/sbt-precompile