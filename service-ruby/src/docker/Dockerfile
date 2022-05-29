# Referencing https://evilmartians.com/chronicles/ruby-on-whales-docker-for-ruby-rails-development
# and https://training.play-with-docker.com/microservice-orchestration/#step-6-swap-python-api-service-with-ruby
# and https://developers.redhat.com/articles/ruby-container
# and https://medium.com/swlh/alpine-slim-stretch-buster-jessie-bullseye-bookworm-what-are-the-differences-in-docker-62171ed4531d
# and https://www.cloudbees.com/blog/build-minimal-docker-container-ruby-apps

ARG RUBY_VERSION=2.7.6
ARG DISTRO_NAME=buster

FROM ruby:$RUBY_VERSION-slim-$DISTRO_NAME
LABEL org.opencontainers.image.source=https://github.com/department-of-veterans-affairs/abd_vro

ARG DISTRO_NAME

# Common dependencies
RUN apt-get update -qq \
  && DEBIAN_FRONTEND=noninteractive apt-get install -yq --no-install-recommends \
    build-essential \
#     gnupg2 \
#     curl \
#     less \
    git \
  && apt-get clean \
  && rm -rf /var/cache/apt/archives/* \
  && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/* \
  && truncate -s 0 /var/log/*log

# Application dependencies
# We use an external Aptfile for this, stay tuned
# COPY Aptfile /tmp/Aptfile
# RUN apt-get update -qq && DEBIAN_FRONTEND=noninteractive apt-get -yq dist-upgrade && \
#   DEBIAN_FRONTEND=noninteractive apt-get install -yq --no-install-recommends \
#     $(grep -Ev '^\s*#' /tmp/Aptfile | xargs) \
#     && apt-get clean \
#     && rm -rf /var/cache/apt/archives/* \
#     && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/* \
#     && truncate -s 0 /var/log/*log

# Configure bundler
ENV LANG=C.UTF-8 \
  BUNDLE_JOBS=4 \
  BUNDLE_RETRY=3

# Store Bundler settings in the project's root
ENV BUNDLE_APP_CONFIG=.bundle

# Uncomment this line if you want to run binstubs without prefixing with `bin/` or `bundle exec`
# ENV PATH /app/bin:$PATH

# Upgrade RubyGems and install the latest Bundler version
RUN gem update --system && \
    gem install bundler

# Create a directory for the app code
RUN mkdir -p /app
WORKDIR /app
COPY . /app/
RUN bundle install

# CMD ["/bin/sh", "-c", "chmod a+x bin/*.*"]
CMD ["./entrypoint.sh"]
