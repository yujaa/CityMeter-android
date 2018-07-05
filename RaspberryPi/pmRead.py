import serial
import datetime
import os
import sys
import stat
import json
import time
import pmDatabase
import pmWiFi
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
serial_channel = serial.Serial('/dev/serial0', baudrate=9600, timeout=1)

#========================================
#Setup
def setup():
    global serial_channel
    try:
        #Open a serial communication channel to recieve data from the PM sensor
        serial_channel = serial.Serial('/dev/serial0', baudrate=9600, timeout=1)
        return serial_channel
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
            return recieved
        
#========================================
#Function to get resulting reading from sensor
def get_result(channel):
    recieved = pm_reader(channel)
    if not (recieved == None):
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
        return result

    
#========================================
#Function to extract the data from the read stream and send it to the server
def wifi_data_extract(channel , session , indoor_):
    try:
        result = get_result(channel)
        while(result == None):
            result = get_result(channel)
        if not (result == None):
            timestamp = str(result['timestamp']).split('.')[0]
            line = '[{\'timestamp\': '+ str(timestamp) +', \'pm2.5\': '+ str(result['pm25']) +  '}]'
            print(result['pm25'] + result['pm10'])
            write_to_db(session, str(result['timestamp']), result['pm25'] + result['pm10'], 0 , indoor_)
            return 'zaba6'
    except BaseException as e:
        print(" wifi data extract error " + str(e))
        on_exit_handler(channel)
        setup()
        return '0<9'
        
#========================================
#Function to handle writing data to the Pi
#Each year has a folder and each week's reading are saved in a single file
#Each line has a timestamp and the PM 2.5 standard reading from the sensor
def write_to_db(session, timestamp, pm, isSent, indoor_):
    pmDatabase.insert_(session, timestamp, pm, isSent, indoor_)
    print('done writing to db')
        


#========================================
#Function for the clean up before exiting
def on_exit_handler (serial_channel):
    serial_channel.close()
    time.sleep(0.02)
    print('exit')
    
#========================================
#Function to catch termination
def set_exit_handler(func):
        import signal
        signal.signal(signal.SIGTERM, func)
        signal.signal(signal.SIGTSTP, func)