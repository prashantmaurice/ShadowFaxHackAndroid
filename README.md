[ShadowFax Hackathon](http://www.venturesity.com/challenge/id/256) (Android Code)
==================================

# Screenshots
![Main screen](/screenshots/1.png) ![Menu closed](/screenshots/2.png) ![Menu closed](/screenshots/3.png) ![Menu closed](/screenshots/4.png)
 
## Context
Delivery guys of ShadowFax face a lot of problems while delivering to the end customers. 
They need to communicate with end customers though phone call and any conversation happening
over it is undocumented, hence automating is difficult in that last-mile delivery

## Solution
A Phone call Tracking App that records any phone call happening by delivery guy.
It does not disturb the ongoing call and at the end of the call we send the back intelligent 
data from the conversation like Address he was talking about, delivery instructions etc
  
## Features
 * Detect Voice conversations and record them
 * Compress and upload conversations to server
 * Decode 3gp into wav through [ffmpeg module](https://ffmpeg.org/)
 * Generate conversation transcripts through the following [python package](https://pypi.python.org/pypi/SpeechRecognition/)
 * Decode Address words from text using address keyword pattern matching
 * Reverse decode latitude and longitude from it using [Google's Geocoding](https://developers.google.com/maps/documentation/geocoding/intro)
 
## Requirements
The library requires Android **API Level 15+** although we testing it for **API Level 7+** onwards

## Inspired from
 * [call-recoder-for-android](https://github.com/riul88/call-recorder-for-android)

## Developed by
 * Prashant Maurice - <sabertoothmaurice@gmail.com> [Linkedin](https://in.linkedin.com/in/prashantmaurice) [Twitter](https://twitter.com/MauricePrashant)
 * Prudhvi Raj - <raj.prudhvi5@gmail.com>
 
 


    