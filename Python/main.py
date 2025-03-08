from flask import Flask, request, jsonify
from dotenv import load_dotenv
import os
import boto3
import json

app = Flask(__name__)

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

@app.route('/generate-workout-plan', methods=['POST'])
def generate_workout_plan():
    data = request.json
    prompt = data.get('prompt', '')

    body = {
        "inputText": prompt,
        "textGenerationConfig": {
            "maxTokenCount": 512,
            "temperature": 0.7,
            "topP": 0.9
        }
    }

    try:
        response = bedrock_runtime.invoke_model(
            modelId="amazon.titan-text-express-v1",
            body=json.dumps(body),
            contentType="application/json",
            accept="application/json"
        )

        response_body = json.loads(response['body'].read())
        generated_text = response_body['results'][0]['outputText']
        return jsonify({"workout_plan": generated_text})
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
