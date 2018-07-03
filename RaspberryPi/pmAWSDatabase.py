#To communicate with DynamoDB on AWS
import boto3
import json
import decimal

# Do not hard code credentials
dynamodb = boto3.client(
    'dynamodb',
    region_name = 'us-east-1',
    # Hard coded strings as credentials, not recommended.
    aws_access_key_id=''
    aws_secret_access_key='',
)
print('DB connected')
def createExposureEntry(timestamp, pm, indoor):
    try:
        response = dynamodb.put_item(
        TableName='citymeter-mobilehub-636316455-user-exposure',
        Item={
               'userId' :{ 'S': '1'},
               'timestamp': { 'S': timestamp},
               'dBA' : {'S': '-1'},
               'latitude' : { 'S' : '-1'},
               'longitude' : {'S' : '-1'},
               'pm25': {'S' : str(pm)},
               'indoor': {'S' : str(indoor)},
            }
        )
    except BaseException as e:
        print('error in writeto server db : ' + str(e))
        return '0<7'