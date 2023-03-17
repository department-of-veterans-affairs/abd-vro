FROM ruby:2.7.6
WORKDIR /bgs-api
COPY Gemfile Gemfile.lock ./
RUN bundle install
COPY . .
EXPOSE 3000
CMD ["./bin/rails", "server", "-b", "0.0.0.0"]
