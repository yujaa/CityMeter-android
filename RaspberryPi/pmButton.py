import RPi.GPIO as GPIO
import time
import os


#========================================
#Setup the button


def indoor_loop():
    GPIO.setmode(GPIO.BCM)#GPIO pin reading mode
    #Assign button
    GPIO.setup(23, GPIO.IN, pull_up_down=GPIO.PUD_UP)#Button to GPIO23
    GPIO.setup(24, GPIO.IN, pull_up_down=GPIO.PUD_UP)#Button to GPIO23
    while True:
        blue_button_state = GPIO.input(23)
        black_button_state = GPIO.input(24)
        if blue_button_state == False:
            print('Blue Button Pressed...')
            return 1
        if black_button_state == False:
            print('Black Button Pressed...')
            return 0