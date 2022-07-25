from config.settings import s3_config
import boto3


def upload_file(file_name: str, bucket: str, file: bytes):
	s3 = boto3.client("s3", aws_access_key_id=s3_config["access_key"], aws_secret_access_key=s3_config["secret_access_key"], aws_session_token=s3_config["session_token"])
	s3.put_object(Body=file, Bucket=bucket, Key=file_name)
