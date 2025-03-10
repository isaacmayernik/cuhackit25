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
    if not data or 'prompt' not in data:
        return jsonify({"error": "Missing or invalid 'prompt' field"}), 400
    
    prompt = data.get('prompt', '')

    # Simulate a response
    generated_text = """
    Here is a sample workout plan:
    - Day 1: Cardio
    - Day 2: Strength Training
    - Day 3: Rest
    - Day 4: Yoga
    - Day 5: HIIT
    """

    return jsonify({"workout_plan": generated_text})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)