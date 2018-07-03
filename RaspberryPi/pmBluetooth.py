import os
import sys
import stat
import json
import time
import fileinput
import datetime
import subprocess
import pmDatabase
from threading import Thread 
##import thread
from bluetooth import *
from datetime import datetime, timedelta

#========================================
#Permissions
#giving permission to use serial profile for the bluetooth connection
command = 'hciconfig hciX piscan'
serial_profile_bt_command = os.system('echo %s|sudo -S %s' % ('', command))
#========================================
#Setup
#Bluetooth socket setup
server_socket=BluetoothSocket( RFCOMM )
client_socket=BluetoothSocket(RFCOMM)
#variable to know when bluetooth is  connected
isConnected = False
    
#========================================
#Function to handle listening to bluetooth port and create a connection
def bluetooth_listen():#server and client sockets as parameters
    global client_socket
    global server_socket
    try:
        server_socket.bind(("",PORT_ANY))
        server_socket.listen(1)
        port = server_socket.getsockname()[1] 
        return port
    except BaseException as e:
        print("listen failed : " + str(e))
        return "0<3"

#========================================
#function to advertise bluetooth device, in order to be found and connected to
def bluetooth_advertise():
    global server_socket
    try:
        command = 'hciconfig hciX piscan'
        serial_profile_bt_command = os.system('echo %s|sudo -S %s' % ('', command))
        port = bluetooth_listen()
        uuid = "00000003-0000-1000-8000-00805f9b34fb" #Bluetooth standard UUID for RFCOMM
        advertise_service( server_socket, "PiSensorServer",service_id = uuid, service_classes = [ uuid, SERIAL_PORT_CLASS ], profiles = [ SERIAL_PORT_PROFILE ] )                    
        print ("Waiting for connection on RFCOMM channel %d" % port)
    except BaseException as e:
        print("bluetooth advertise failed : " + str(e))
        return "0<4"

#========================================
#function to accept bluetooth connection
def bluetooth_on_accept():
    global server_socket
    global client_socket
    global isConnected
    try:
        client_socket, client_info = server_socket.accept()
        print ("Accepted connection from ", client_info)
        timestamp = client_socket.recv(1024)
        print('recieved time: ' + str(timestamp))
##        command = 'hwclock --set --date "8/11/2013 23:10:45"'
##        set_time_command = os.system('echo %s|sudo -S %s' % ('', command))
        isConnected = True
    except BaseException as e:
        print('bluetooth connection failed: ' + str(e))
        isConnected = False
        return "0<5"
        
#========================================
#function to send the data over bluetooth
def bluetooth_send( data ):#client socket as parameter
    global client_socket
    try:
        print('sending data over bluetooth')
        client_socket.send(data)
        return "Zaba6"
    except BaseException as e:
        print('send error : ' +  str(e))
        return "0<7"

#=======================================
#function to send the data over bluetooth
def bluetooth_close_connections():
    global server_socket
    global client_socket
    server_socket.close()
    server_socket= None
    server_socket= BluetoothSocket( RFCOMM )
    client_socket.close()
    client_socket = None
    client_socket = BluetoothSocket( RFCOMM )

#========================================
#function to restart the script on termination
def restart_script():
    python = sys.executable
    os.execl(python, python, * sys.argv)
    
#========================================
#Function for the clean up before exiting
def on_exit_handler ():
    bluetooth_close_connections()
    time.sleep(0.02)
    print('exit')
    



#========================================
#Function to catch termination
def set_exit_handler(func):
        import signal
        signal.signal(signal.SIGTERM, func)
        signal.signal(signal.SIGTSTP, func)