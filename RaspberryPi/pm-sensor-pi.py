import serial
import os
import sys
import stat
import json
import time
import fileinput
from threading import Thread 
##import thread
from bluetooth import *
from datetime import datetime, timedelta

#========================================
#Permissions
#giving permission to use serial profile for the bluetooth connection
command = 'hciconfig hci0 piscan'
serial_profile_bt_command = os.system('echo %s|sudo -S %s' % ('', command))
data_path = "/home/pi/Desktop/sensor-data/2018/"

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
def bluetooth_listen(ssocket):#server and client sockets as parameters
    global client_socket
    try:
        ssocket.listen(1)
        port = ssocket.getsockname()[1] 
        return port
    except BaseException as e:
        print("listen failed : " + str(e))
        return "0<3"

#========================================
#function to advertise bluetooth device, in order to be found and connected to
def bluetooth_advertise():
    try:
        uuid = "00000003-0000-1000-8000-00805f9b34fb" #Bluetooth standard UUID for RFCOMM
        advertise_service( ssocket, "PiSensorServer",service_id = uuid, service_classes = [ uuid, SERIAL_PORT_CLASS ], profiles = [ SERIAL_PORT_PROFILE ], )                    
        print ("Waiting for connection on RFCOMM channel %d" % port)
    except BaseException as e:
        print("bluetooth advertise failed : " + str(e))
        return "0<4"

#========================================
#function to accept bluetooth connection
def bluetooth_on_accept():
    try:
        client_socket, client_info = ssocket.accept()
        print ("Accepted connection from ", client_info)
        isConnected = True
    except BaseException as e:
        print('bluetooth connection failed: ' + str(e))
        isConnected = False
        return "0<5"

#========================================
#function to get the name of the first day in the week of the given date
def get_week_name():
        day = str(datetime.today())
        dt = datetime.strptime(day, '%Y-%m-%d %H:%M:%S')
        start = dt - timedelta(days = dt.weekday())
        year,week,day = start.isocalendar()
        weekname = datetime.strftime(start, '%m-%d-%Y')
        return weekname

#========================================
#function to read a data line from the stored data
def get_data_line():
    try:
        file_path = data_path + get_week_name() + ".txt"
        line = ""
        os.system('echo %s|sudo -S %s' % ('', 'chmod 777 ' + file_path))
        with open(file_path, 'a') as myfile:
            myfile.readline(line)#I need to either set false to true in the isSent tupple or delete line: not a very good idea?
            myfile.close()
            if(line.find("False")):
                return line
            else:
                return "0<6"
    except BaseException as e:
        print ('Reading line failed : ' + str(e))
        return "0<6"

#========================================
#Function to mark the lines sent to mobile as read in the file to avoid duplication
def mark_read(myline):
    file_path = data_path + get_week_name() + ".txt"
    for line in fileinput.input(file_path, inplace=True): 
        if(line == myline):
            newline = myline.replace('False', 'True')
            print line.rstrip().replace(line, newline )

#========================================
#function to send the data over bluetooth
def bluetooth_send(csocket, data ):#client socket as parameter
##    print('in sender')
    try:
        csocket.send(data)
        #return True
    except BaseException as e:
        print('send error : ' +  str(e))
        return "0<7"
    
#========================================
#function to send the data over bluetooth
def bluetooth_close_connections(ssocket, csocket):
    ssocket.close()
    ssocket = null
    ssocket= BluetoothSocket( RFCOMM )
    csocket.close()
    csocket = null
    csocket = BluetoothSocket( RFCOMM )

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
        bluetooth_listen(server_socket)
        bluetooth_advertise()
        bluetooth_on_accept()
        #while (True):
        for i in range(0,4):
            line_ = get_data_line()
            check = bluetooth_send(client_socket, line_)
            if not (check == "0<7"):
                e5 = mark_read(line_)
            else:
                bluetooth_close_connections(server_socket, client_socket)
                bluetooth_listen(server_socket)
                bluetooth_advertise()
                bluetooth_on_accept()
                line_ = get_data_line()

    except BaseException as e:
        print("__main__ e1 " + str(e))
        on_exit_handler()
    except BluetoothError as e:
        print("__main__ e2 " + str(e))
        on_exit_handler()
    except SystemExit as e:
        print("__main__ e3 " + str(e))
        on_exit_handler()
    