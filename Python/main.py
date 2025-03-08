import os
import boto3
from dotenv import load_dotenv
import json

load_dotenv()
access_key_id = os.getenv('AWS_ACCESS_KEY_ID')
secret_access_key = os.getenv('AWS_SECRET_ACCESS_KEY')
session_token = os.getenv('AWS_SESSION_TOKEN')
region = os.getenv('AWS_REGION')

print(access_key_id)
print(secret_access_key)
print(region)


bedrock_runtime = boto3.client(
    'bedrock-runtime',
    aws_access_key_id=access_key_id,
    aws_secret_access_key=secret_access_key,
    aws_session_token=session_token,
    region_name=region
)

prompt = " Generate a 7-day workout plan for a beginner"

body = {
    "inputText": prompt,
    "textGenerationConfig":{
        "maxTokenCount": 512,
        "temperature": 0.7,
        "topP": 0.9
    }
}

try:
    response = bedrock_runtime.invoke_model(
        modelId= "amazon.titan-text-express-v1",
        body = json.dumps(body),
        contentType = "application/json",
        accept = "application/json"
    )
     

    response_body = json.loads(response['body'].read())
    generated_text = response_body['results'][0]['outputText']
    print("Generated Text:", generated_text)
    print("Response: ", response)
except Exception as e:
    print("Error invoking Titan G1 Express:", e)
   
