##General file to communicate between BT and wifi interfaces

#import pm modules
import pmDatabase
import pmWiFi
import pmBluetooth
import pmRead
import pmButton
import pmAWSDatabase

#import libraries
import os
import dbus
import sys
import threading
import RPi.GPIO as GPIO
import time
from bluetooth import *
from datetime import datetime, timedelta
from threading import Thread
from sqlalchemy import *
from sqlalchemy.orm import *
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, Integer, String

#========================================
#Database setup
dbConnection = create_engine('sqlite:///pmDB.db')
metadata = MetaData(dbConnection)

pmDatabase.Base.metadata.create_all(dbConnection)
#Session facory to handle multithreading
session_factory = sessionmaker(bind=dbConnection)
Session = scoped_session(session_factory)

channel = pmRead.setup()

inThread = ''
indoor_ = -1

def gpio_setup():
    try:
        if not (indoor_ == -1):
            GPIO.cleanup()
    except:
        print("no gpio channels")
    GPIO.setmode(GPIO.BCM)#GPIO pin reading mode
    #Assign buttons
    GPIO.setup(23, GPIO.IN, pull_up_down=GPIO.PUD_UP)#Button indoor to GPIO23
    GPIO.setup(24, GPIO.IN, pull_up_down=GPIO.PUD_UP)#Button outdoor to GPIO24

def set_indoor():
    global indoor_
    while(True):
        gpio_setup()
        indoor_ = pmButton.indoor_loop()

def read_helper(read_session):
    b = pmRead.wifi_data_extract(channel, read_session, indoor_)
        
#========================================
#Function to read data from sensor using the pmRead script when on WiFi
def read():
    global channel
    try:
        while True:
            read_session = Session()
            read_helper(read_session)
            Session.remove()
    except BaseException as e:
        print("read error : " + str(e))
        pmRead.on_exit_handler(channel)
        Session.remove()
    except SystemExit as e:
        print("read exit:  " + str(e))
        pmRead.on_exit_handler(channel)
        Session.remove()

#========================================
#Function to handle Bluetooth communication
def bluetooth_handler_helper():
        check_adv = pmBluetooth.bluetooth_advertise()
        if not (check_adv == '0<4'):
            check_accept = pmBluetooth.bluetooth_on_accept()
            while not (check_accept == '0<5'):
                bt_session = Session()
                line_ = get_data_line(bt_session)
                if not (line_ == "0<6"):
                    check = pmBluetooth.bluetooth_send(line_)
                    if not (check == "0<7"):
                        mark = mark_read(line_, bt_session)
                    else:
                        pmBluetooth.bluetooth_close_connections()
                        pmBluetooth.bluetooth_advertise()
                        check_accept = pmBluetooth.bluetooth_on_accept()
                else:
                    line_= get_data_line(bt_session)
                Session.remove()
        else:
            pmBluetooth.bluetooth_close_connections()
            
#========================================
#Function to handle Bluetooth communication
def bluetooth_handler():
    command = 'hciconfig hciX piscan'
    serial_profile_bt_command = os.system('echo %s|sudo -S %s' % ('', command))
    line = "0<6"
    try:
        while not (pmWiFi.check_internet()):
            bluetooth_handler_helper()
            time.sleep(5)
        return 0
    except BaseException as e:
        print("bt error: " + str(e))
        Session.remove()
        pmBluetooth.on_exit_handler()
    except BluetoothError as e:
        print("bt bluetooth error: " + str(e))
        Session.remove()
        pmBluetooth.on_exit_handler()
    except SystemExit as e:
        print("bt exit handler error: " + str(e))
        Session.remove()
        pmBluetooth.on_exit_handler()

#========================================
#Function to handle sending to server
def send_to_server_helper():
    try:
        server_session = Session()
        line = get_data_line_server(server_session)
        line = str(line).split(', ')
        timestamp = line[0].split('=')[1].replace('\'','')
        pm = line[1].split('=')[1].replace('\'' , '')
        indoor = line[3].split('=')[1].replace('\'' , '')
        if (not line == "0<6"):
            check = pmAWSDatabase.createExposureEntry(timestamp, pm, indoor)
            if not (check == "0<7"):
                mark = mark_read_server(timestamp, server_session)
        Session.remove()
    except BaseException as e:
        print('server helper error ' + str(e))
        return "0<11"
        
        
#========================================
#Function to handle sending to server
def send_to_server():
    try:
        while (pmWiFi.check_internet()):
            check = send_to_server_helper()
            time.sleep(5)
        return 0
    except BaseException as e:
        print("wifi error " + str(e))
        Session.remove()
    except SystemExit as e:
        print("wifi exit  " + str(e))
        Session.remove()
        
#========================================
#Function to handle sending to server        
def sync():
    while True:
        #check wifi
        if (pmWiFi.check_internet()):
            inThread = 'wifi'
            send_to_server()
        else:
            #not connected to wifi, try bluetooth
            inThread= 'bt'
            bluetooth_handler()
            
readThread = Thread(target=read)
syncThread = Thread(target=sync)
buttonThread = Thread(target=set_indoor)

#========================================
#Function to handle exit
def on_exit_handler(arg1 = None, arg2 = None):
    global channel
    global readThreadwifi
    global wifiThread
    global readThreadbt
    global btThread
    try:
        dbConnection.dispose()
        signal.pthread_kill(readThread.ident, 0)
        signal.pthread_kill(syncThread.ident, 0)
        pmBluetooth.on_exit_handler()
        pmRead.on_exit_handler(channel);
    except:
        print('exit failed')
    sys.exit(0)
    
#========================================  
#Function to set exit handler
def set_exit_handler(func):
    import signal
    signal.signal(signal.SIGTERM, func)
    signal.signal(signal.SIGTSTP, func)

#=======================================
#Function to get an unsent data line
def get_data_line(session):
    try:
        line = pmDatabase.select_row_by_sent(session)
        if not (line == None):
            line = str(line).replace('<PMLine(', '')
            line = str(line).replace(')>', '')
            line = str(line).split(', ')
            timestamp = line[0].split('=')[1].replace('\'','')
            pm = line[1].split('=')[1].replace('\'' , '')
            isSent = line[2].split('=')[1].replace('\'' , '')
            indoor = line[3].split('=')[1].replace('\'' , '')
            result = '[{\'timestamp\': '+ timestamp +', \'pm2.5\': '+ pm + ', \'indoor\': ' + indoor + '}]'
            return result
        else:
            return "0<6"
    except BaseException as e:
        print('get line error : ' + str(e))
        return "0<6"
    
def get_data_line_server(session):
    try:
        line = pmDatabase.select_row_by_sent(session)
        if not (line == None):
            line = str(line).replace('<PMLine(', '')
            line = str(line).replace(')>', '')
            return line
        else:
            return "0<6"
    except BaseException as e:
        print('get line error : ' + str(e))
        return "0<6"

#=======================================
#Function to mark sent data line as Sent
def mark_read(line , session):
    print('marking as read')
    fromstr = line.find('\'timestamp\': ' )
    tostr = line.find(',', fromstr+13)
    timestamp = line[fromstr +13 : tostr]
    check = pmDatabase.update_(session, timestamp)
    return timestamp

#=======================================
#Function to mark sent data line as Sent
def mark_read_server(timestamp , session):
    print('marking as read')
    check = pmDatabase.update_(session, timestamp)
    return timestamp

#========================================
#Main entry point
if __name__ == '__main__':
    set_exit_handler(on_exit_handler)
    try:
        #Thread to read data
##        buttonThread.start()
        readThread.start()
        syncThread.start()
        set_indoor()
    except BaseException as e:
        if(inThread == 'bt'):
            pmBluetooth.on_exit_handler()
        print('error in main : ' + str(e))
        on_exit_handler()