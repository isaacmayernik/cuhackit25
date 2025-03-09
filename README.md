# cuhackit25

Developed by Joseph Skapin and Isaac Mayernik for the Clemson University Hackathon 2025, this is an Android app that utilizes Kotlin and Python to access the AWS and Bedrock services to generate a workout plan based on user-entered information using a text chat model.

[Watch our YouTube Demo](https://youtu.be/YLmeqjT_Fqs)

The app has been updated to work without AWS services, but will generate the same plan every time. By default, it will not use them, so please move the code from 'OLD main.py' to 'main.py' if you want to use the AWS services and generate new plans using the text chat model. If you do, you must have a .env file in the 'Python' directory with the correct variable names. 

You must always run the 'start_flask.bat' when running the app.
