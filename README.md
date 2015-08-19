Android Phoenix Demo
=====

This is a demo of an Android app using the [JavaPhoenixChannels](https://github.com/eoinsha/JavaPhoenixChannels) Phoenix client to communicate with the [Phoenix Chat Example](https://github.com/chrismccord/phoenix_chat_example).

## Running the demo
1. Make sure to clone the [Phoenix Chat Example](https://github.com/chrismccord/phoenix_chat_example) and have it running.
2. Clone this project
3. Open in Android Studio
4. Go to app/build.gradle and update the "buildConfigField" "HOST" value to the IP that the phoenix chat example is running on (you will have to use the IP and not localhost).
5. Run the app in debug mode
6. When the app is running in the emulator (or phone), you should see the chat messages from the server. You can also send messages as well.