import serial
import datetime
import os
import sys
import stat
import json
import time
import RPi.GPIO as GPIO
from threading import Thread

#========================================
#Permissions
#Change the serial port access permission
#os.chmod('/dev/serial0', stat.S_IRUSR | stat.S_IRGRP | stat.S_IROTH | stat.S_IWUSR | stat.S_IWGRP | stat.S_IWOTH | stat.S_IXUSR | stat.S_IXGRP | stat.S_IXOTH)
#for sensor channel
command = 'chmod 777 /dev/serial0'
permission_command = os.system('echo %s|sudo -S %s' % ('', command))
#For data storing path
data_store_path = '/home/pi/Desktop/sensor-data/'
data_store_command = 'chmod 777 ' + data_store_path
data_store_permission_command = os.system('echo %s|sudo -S %s' % ('', data_store_command))
#giving permission to use serial profile for the bluetooth connection
command = 'hciconfig hci0 piscan'
serial_profile_bt_command = os.system('echo %s|sudo -S %s' % ('', command))
#Avoid warnings from GPIO
GPIO.setwarnings(False)

#========================================
#Setup
def setup():
    global serial_channel
    try:
        #Open a serial communication channel to recieve data from the PM sensor
        serial_channel = serial.Serial('/dev/serial0', baudrate=9600, timeout=3)
    except BaseException as e:
        print(str(e)) 


#========================================
#Function to check and read data from channel
#First checks if correct datastrem by matching the first two fixed bytes
#The first two bytes are 0x42 and 0x4d
def pm_reader(channel):
    recieved = b''
    fixed1 = channel.read()
    if fixed1 == b'\x42':
        fixed2 = channel.read()
        if fixed2 == b'\x4d':
            recieved += fixed1 + fixed2
            recieved += channel.read(28)
##            print('read success ' + str(recieved))
            return recieved
        
#========================================
#Function to extract the data from the read stream
def data_extract(channel):
    try:
        recieved = pm_reader(channel)
        if(recieved == None):
            return 0
        else:
##            print(recieved)
            #formatting data because each piece of data is represented by two bytes
            result = {'timestamp': datetime.datetime.now(),
            'apm10': recieved[4] * 256 + recieved[5],
            'apm25': recieved[6] * 256 + recieved[7],
            'apm100': recieved[8] * 256 + recieved[9],
            'pm10': recieved[10] * 256 + recieved[11],
            'pm25': recieved[12] * 256 + recieved[13],
            'pm100': recieved[14] * 256 + recieved[15],
            'gt03um': recieved[16] * 256 + recieved[17],
            'gt05um': recieved[18] * 256 + recieved[19],
            'gt10um': recieved[20] * 256 + recieved[21],
            'gt25um': recieved[22] * 256 + recieved[23],
            'gt50um': recieved[24] * 256 + recieved[25],
            'gt100um': recieved[26] * 256 + recieved[27]
             }
            #List to save the data for sending over bluetooth
            data_list = []
            data_list.append({
            'timestamp': result['timestamp'].strftime("%b %d %Y %H:%M:%S"),
            'pm2.5':result['pm25'],
            'isSent' : False})
            write_to_file(data_list, result['timestamp'])
    except BaseException as e:
##        print("data extract e1 " + str(e))
        return 0
        
#========================================
#Function to handle writing data to the Pi
#Each year has a folder and each week's reading are saved in a single file
#Each line has a timestamp and the PM 2.5 standard reading from the sensor
def write_to_file(data, date):
    year,week,day = date.isocalendar()
    if not(os.path.isdir(data_store_path + str(year))):
        os.mkdir(data_store_path + str(year),0o777)
    file_path = data_store_path+ str(year) +'/'+ str(week)+ '.txt'
    file_command = 'chmod 777 ' + file_path
    data_store_permission_command = os.system('echo %s|sudo -S %s' % ('', file_command))
    with open(file_path , 'a') as outfile:
        outfile.write(str(data) + '\n')
        
#========================================
#Function to turn led on
def led_on():
    #LED to be on when the sensor is reading and off when not
    GPIO.cleanup()#to clean up the port and disconnect it to previous codes
    GPIO.setmode(GPIO.BCM) # to use BCM pin numbers
    pin = 25 # to save the number of the GPIO pin used for the LED 
    GPIO.setup(pin, GPIO.OUT) # setup pin to OUT
    GPIO.output(pin,True)
    
#========================================
#Function to turn led off
def led_off():
    #LED to be on when the sensor is reading and off when not
    GPIO.cleanup()#to clean up the port and disconnect it to previous codes
    GPIO.setmode(GPIO.BCM) # to use BCM pin numbers
    pin = 25 # to save the number of the GPIO pin used for the LED 
    GPIO.setup(pin, GPIO.OUT) # setup pin to OUT
    GPIO.output(pin,False)

#========================================
#Function for the clean up before exiting
def on_exit_handler ():
    serial_channel.close()
    led_off()
    time.sleep(0.02)
    print('exit')
    sys.exit()
    #restart_script
    #========================================
#Function to catch termination
def set_exit_handler(func):
        import signal
        signal.signal(signal.SIGTERM, func)
        signal.signal(signal.SIGTSTP, func)
    
#========================================
#main entry point
if __name__== "__main__":
    #Set Termination handler
    setup()
    set_exit_handler(on_exit_handler)
    try:
        led_on()
        while True:
            b = data_extract(serial_channel)
            if(b == 0):
                b= data_extract(serial_channel)
    except BaseException as e:
        print("__main__ e1 " + str(e))
        on_exit_handler()
    except BluetoothError as e:
        print("__main__ e2 " + str(e))
        on_exit_handler()
    except SystemExit as e:
        print("__main__ e3 " + str(e))
        on_exit_handler()