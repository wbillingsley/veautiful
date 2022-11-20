FROM openjdk:17-jdk-slim-bullseye

# Install some basic development utilities
RUN apt-get update && apt-get install -y \ 
  curl \
  git \
  python3 \
  zip \
  && rm -rf /var/lib/apt/lists/* \
  && curl -fsSL https://deb.nodesource.com/setup_17.x | bash \
  && apt-get install -y nodejs \
  && npm install -g yarn \
  && rm -rf /var/lib/apt/lists/* 

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
# Commented out because multi-arch buildx takes too long doing this.
#
#ARG SBT_VERSION=1.6.1
#ARG SCALA_VERSION=3.1.0
#ARG SCALAJS_VERSION=1.8.0
#RUN mkdir -p /sbtprecacheproj/project && \
#  cd /sbtprecacheproj && \
#  echo sbt.version=${SBT_VERSION} > project/build.properties && \
#  echo 'addSbtPlugin("org.scala-js" % "sbt-scalajs" % "'${SCALAJS_VERSION}'")' > project/plugins.sbt && \
#  echo '@main def hello = println("hello")' > main.scala && \
#  echo 'enablePlugins(ScalaJSPlugin); scalaVersion := "'${SCALA_VERSION}'"' > build.sbt && \
#  sbt fastLinkJS && cd / && rm -r -f /tmp/sbt-precompile

CMD bash