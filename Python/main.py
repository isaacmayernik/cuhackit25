import os
import boto3
from dotenv import load_dotenv
import json

load_dotenv()
access_key_id = os.getenv('AWS_ACCESS_KEY_ID')
secret_access_key = os.getenv('AWS_SECRET_ACCESS_KEY')
session_token = os.getenv('AWS_SESSION_TOKEN')
region = os.getenv('AWS_REGION')

bedrock_runtime = boto3.client(
    'bedrock-runtime',
    aws_access_key_id=access_key_id,
    aws_secret_access_key=secret_access_key,
    aws_session_token=session_token,
    region_name=region
)

def generate_workout_plan(prompt:str) -> str:
    body = {
        "inputText": prompt,
        "textGenerationConfig":
        {
            "maxTokenCount": 512,
            "temperature": 0.7,
            "topP": 0.9
    }
 }


try:
    response = bedrock_runtime.invoke_model(
        modelId= "amazon.titan-text-express-v1",
        body = json.dumps(generate_workout_plan),
        contentType = "application/json",
        accept = "application/json"
    )
     
    response_body = json.loads(response['body'].read())
    generated_text = response_body['results'][0]['outputText']
    print(generated_text)
except Exception as e:
    print("Error invoking Titan G1 Express:", e)
   
