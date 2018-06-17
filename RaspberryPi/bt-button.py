import RPi.GPIO as GPIO
import time
import os


#========================================
#Setup the button
GPIO.setmode(GPIO.BCM)#GPIO pin reading mode
#Assign button
GPIO.setup(23, GPIO.IN, pull_up_down=GPIO.PUD_UP)#Button to GPIO23

#========================================
#function to restart the script on termination
def restart_script():
    python = sys.executable
    os.execl(python, python, * sys.argv)
    
#========================================
#Function for the clean up before exiting
def on_exit_handler ():
    GPIO.cleanup()
    print('error')
    time.sleep(1)
    restart_script()
    
#========================================
#Function to catch termination
def set_exit_handler(func):
        import signal
        signal.signal(signal.SIGTERM, func)
        signal.signal(signal.SIGTSTP, func)
        
#========================================
#This should have a main entry point
#The call to start the script should be in a thread
try:
    set_exit_handler(on_exit_handler)
    while True:
        button_state = GPIO.input(23)
        if button_state == False:
             print('Button Pressed...')
             command = 'python3 /home/pi/Desktop/pm-sensor-pi.py'
             permission_command = os.system('echo %s|sudo -S %s' % ('', command))
             time.sleep(0.2)
except BaseException:
        on_exit_handler()
except SystemExit:
        on_exit_handler()
    
    