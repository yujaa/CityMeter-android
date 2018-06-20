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
#giving permission to use serial profile for the bluetooth connection
command = 'hciconfig hci0 piscan'
serial_profile_bt_command = os.system('echo %s|sudo -S %s' % ('', command))

#========================================
#Setup
#Bluetooth socket setup
server_socket=BluetoothSocket( RFCOMM )
server_socket.bind(("",PORT_ANY))
client_socket=BluetoothSocket(RFCOMM)
#variable to know when bluetooth is  connected
isConnected = False
#variable to flag if all data is synced or not
isSynced = False
            

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
    except BaseException as e:
        print('Connecting failed ' + str(e))
        isConnected = False

#========================================
#function to send the data over bluetooth
def bluetooth_send(csocket, data ):#client socket as parameter
##    print('in sender')
    try:
        csocket.send(data)
        return True
    except:
        print('send error ')
        on_exit_handler()
    
#========================================
#function to send the data over bluetooth
def bluetooth_close_connections(ssocket, csocket):
    ssocket.close()
    csocket.close()

#========================================
#function to restart the script on termination
def restart_script():
    python = sys.executable
    os.execl(python, python, * sys.argv)
    
#========================================
#Function for the clean up before exiting
def on_exit_handler ():
    bluetooth_close_connections(server_socket, client_socket)
    time.sleep(0.02)
    print('exit')
    sys.exit()
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
        bluetooth_connection_handler(server_socket, client_socket)
##        bt_connect_thrd = Thread(target= bluetooth_connection_handler, args=(server_socket, client_socket,))
##        bt_connect_thrd.start()
    except BaseException as e:
        print("__main__ e1 " + str(e))
        on_exit_handler()
    except BluetoothError as e:
        print("__main__ e2 " + str(e))
        on_exit_handler()
    except SystemExit as e:
        print("__main__ e3 " + str(e))
        on_exit_handler()
    