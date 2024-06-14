# README

The `svc-bgs-api` codebase is written in ruby. The entry point is `main_consumer.rb`, which sets up subscriptions to RabbitMQ queues and defines behavior for handling messages.

## Local dev setup

instructions validated on a Mac (Apple Silicon chip)

### Prerequisites
1. A ruby version manager, such as [`rvm`](https://rvm.io/rvm/install).
    Once installed, activate the version of ruby used by `svc-bgs-api`. The version is documented in `src/.ruby-version`.  

2. The ruby package manager `bundler`. (`gem install bundler`)


### Running the code

_from the src/ directory:_

1. Install the dependencies: `bundle install`. If successful, this should be the output of `bundle check`:
``` 
% bundle check
The Gemfile's dependencies are satisfied
```
2. To execute the main script:
```
bundle exec ruby main_consumer.rb
```
To trigger message handling, add a message to a queue in the [rabbitMQ management interface](https://www.cloudamqp.com/blog/part3-rabbitmq-for-beginners_the-management-interface.html) (typically http://localhost:15672/, [default login](https://www.rabbitmq.com/docs/access-control#default-state)). Queue names are listed in `src/config/constants.rb`.  


3. To run unit tests:
> bundle exec rspec


4. To run integration tests:
> bundle exec ruby integrationTest/run_integration_tests.rb