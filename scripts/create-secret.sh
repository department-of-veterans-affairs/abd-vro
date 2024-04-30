#!/usr/bin/env bash

# encode string to base64
base64_encode() {
    echo -n "$1" | base64
}

print_usage() {
    echo "Usage: $0 [-n, --dry-run] <env> <secret_name> KEY_1=VALUE_1 KEY_2=VALUE_2 ..."
    echo "Options:"
    echo "  -n, --dry-run     Preview the kubectl command without applying the Secret."
}
# check for dry_run flag
dry_run=false
while [[ "$#" -gt 0 ]]; do
    case "$1" in
        -n|--dry-run)
            dry_run=true
            shift
            ;;
        *)
            break
            ;;
    esac
done

# Check for minimum number of arguments provided
if [ "$#" -lt 3 ]; then
    echo "Error: Insufficient arguments."
    print_usage
    exit 1
fi

: "${TARGET_ENV:=$1}"
secret_name="$2"

# validate target env
case "${TARGET_ENV}" in
  dev|qa|sandbox) choice="y"; echo "Executing $0 for env: $TARGET_ENV";;
  prod-test|prod) read -rp "Executing $0 for env: $TARGET_ENV Please Confirm (y/n)?" choice;;
  *)  echo "Unknown TARGET_ENV: $TARGET_ENV"
      exit 3
      ;;
esac

# prod environments prompt for confirmation
case "$choice" in
    y|Y ) echo "$TARGET_ENV confirmed: yes";;
    * ) echo "$TARGET_ENV was not confirmed"
        exit 4
        ;;
esac

shift 2  # Remove the first two arguments (secret name and namespace)

SECRET_MAP=""
whitespace="  "
# Process each key-value pair
for arg in "$@"; do
    if [[ "$arg" =~ ^([^=]+)=(.+)$ ]]; then
        key="${BASH_REMATCH[1]}"
        value="${BASH_REMATCH[2]}"

        # Encode value to base64
        encoded_value=$(base64_encode "$value")

        # Append the key-value pair to YAML content
        SECRET_MAP+="${whitespace}$key: \"$encoded_value\""
        whitespace="\n  "
    else
        echo "Error: Invalid key-value pair format: $arg"
        print_usage
        exit 1
    fi
done

yaml_content=$(./scripts/echo-secret-yaml.sh "$secret_name" "$SECRET_MAP")

# Preview or apply the Secret YAML using kubectl
if [ "$dry_run" = true ]; then
    echo "Dry run: kubectl -n \"va-abd-rrd-$TARGET_ENV\" replace --force -f - <<EOF$yaml_content"
    echo "EOF"
else

    echo "$yaml_content" | \
        kubectl -n "va-abd-rrd-$TARGET_ENV" replace --force -f -
    # Apply the Secret YAML using kubectl in a heredoc cat statement
    #cat <<EOF | kubectl apply -n "$TARGET_ENV" -f -
    #$yaml_content
fi