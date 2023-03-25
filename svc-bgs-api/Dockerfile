FROM ruby:2.7.6
WORKDIR /bgs-api
COPY Gemfile Gemfile.lock ./
RUN bundle install
COPY . .
CMD ["./entrypoint.sh"]
