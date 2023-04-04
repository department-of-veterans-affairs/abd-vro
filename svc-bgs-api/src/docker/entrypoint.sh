# This will be called by entrypoint-wrapper.sh,
# after it sets environment variables for secrets

exec bundle exec ruby main_consumer.rb
