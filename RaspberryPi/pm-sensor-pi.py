import serial
import datetime
import os
import sys
import stat
import json
import time
import RPi.GPIO as GPIO
from threading import Thread 
##import thread
from bluetooth import *


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

#========================================
#Setup
#Open a serial communication channel to recieve data from the PM sensor
serial_channel = serial.Serial('/dev/serial0', baudrate=9600, timeout=3)
#Bluetooth socket setup
server_socket=BluetoothSocket( RFCOMM )
server_socket.bind(("",PORT_ANY))
client_socket=BluetoothSocket(RFCOMM)
#variable to know when bluetooth is  connected
isConnected = False
#variable to flag if all data is synced or not
isSynced = False
#Avoid warnings from GPIO
GPIO.setwarnings(False)

#========================================
#Function to check and read data from channel
#First checks if correct datastrem by matching the first two fixed bytes
#The first two bytes are 0x42 and 0x4d
def pm_reader(channel):
    recieved = b''
    while True:
        fixed1 = channel.read()
        if fixed1 == b'\x42':
            fixed2 = channel.read()
            if fixed2 == b'\x4d':
                recieved += fixed1 + fixed2
                recieved += channel.read(28)
                return recieved
            
#========================================
#Function to extract the data from the read stream
def data_extract(channel):
    global isConnected
    global isSynced
    global client_socket
    while True:
        recieved = pm_reader(channel)
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
        'pm2.5':result['pm25']
        })#for sending to mobile
        #For better handling the sending and writing a handshaking protocol with the mobile should be implemented
        #But the hold back is that it is time and processing power consuming
##        print('is connected? ' + str(isConnected))
##        print(str(data_list))
        if (isConnected):
            if (bluetooth_send(client_socket, str(data_list))):#Make sure the data is sent
                data_list.append({'isSent' : True})
                write_to_file(data_list, result['timestamp'])
##                print('data sent ')
            else:
                data_list.append({'isSent' : False})
                write_to_file(data_list, result['timestamp'])
        else:
            data_list.append({'isSent' : False})
            write_to_file(data_list, result['timestamp'])
        
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
#function to handle data that are not sent to the phone immediately
def sync_handler(csocket):
##    print('in handler')
    try:
        while True:
            year,week,day = datetime.datetime.now().isocalendar()
            with open(data_store_path+ str(year) +'/' + str(week)+ '.txt') as f:
                content = f.readlines()
                for line in content:
##                print(line)
                #check if already sent or not
                    if (line.find('True') == -1) and not (line.find('False') == -1) :
##                  csocket.send(line)
                        bluetooth_send(csocket, line )
##                        print ("sent [%s]" % line)
            isSync = True
    except IOError:
        isSync = False
        pass
    
#========================================
#Function to handle listening to bluetooth port and create a connection
def bluetooth_connection_handler(ssocket, csocket):#server and client sockets as parameters
    global isConnected
    global isSynced
    global client_socket
##    print('in bt connection')
    try:
        ssocket.listen(1)
        port = ssocket.getsockname()[1] 
        uuid = "00000003-0000-1000-8000-00805f9b34fb" #Bluetooth standard UUID for RFCOMM
        advertise_service( ssocket, "PiSensorServer",service_id = uuid, service_classes = [ uuid, SERIAL_PORT_CLASS ], profiles = [ SERIAL_PORT_PROFILE ], )                    
        print ("Waiting for connection on RFCOMM channel %d" % port)
        client_socket, client_info = ssocket.accept()
        print ("Accepted connection from ", client_info)
        isConnected = True
##        print('is connected2? ' + str(isConnected))
##        sync_handler(csocket)
    except:
        print('Connecting failed ' )
        isConnected = False

#========================================
#function to send the data over bluetooth
def bluetooth_send(csocket, data ):#client socket as parameter
##    print('in sender')
##    try:
    csocket.send(data)
    return True
##    except:
##        print('send error : ' + data)
    
#========================================
#function to send the data over bluetooth
def bluetooth_close_connections(ssocket, csocket):
    ssocket.close()
    csocket.close()
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
#function to restart the script on termination
def restart_script():
    python = sys.executable
    os.execl(python, python, * sys.argv)
    
#========================================
#Function for the clean up before exiting
def on_exit_handler ():
    serial_channel.close()
    bluetooth_close_connections(server_socket, client_socket)
    led_off()
    time.sleep(5)
    #restart_script()
    
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
    set_exit_handler(on_exit_handler)
    try:
        led_on()
        bt_connect_thrd = Thread(target= bluetooth_connection_handler, args=(server_socket, client_socket,))
        bt_connect_thrd.start()
        read_data_thrd = Thread(target= data_extract, args = (serial_channel, ))
        read_data_thrd.start()
    except BaseException:
        on_exit_handler()
    except SystemExit:
        on_exit_handler()
    