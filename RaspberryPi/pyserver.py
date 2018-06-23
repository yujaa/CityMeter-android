import socket
import time
import os
import sys
from datetime import datetime, timedelta

#TODO: check if any of the files in active needs to be moved to archive and move it
#=============================
#initializing variables
TCP_IP = '0.0.0.0' #'ec2-34-229-219-45.compute-1.amazonaws.com'
TCP_PORT = 80
BUFFER_SIZE = 10240
#Create a socket to store the connection made to file
connection = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
ssocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
#path to store the files
active_path = '/home/ubuntu/city-meter/active/'
archive_path = '/home/ubuntu/city-meter/archive/'

trials= 0

#=============================
#Function to set up the server
#Set ip address and port
#Start listening

def server_setup():
        global ssocket
        try:
                ssocket = None
                ssocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                ssocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
                ssocket.bind((TCP_IP, TCP_PORT))
        except BaseException as e:
                print("server error in setup: " + str(e))

def server_listen():
        try:
                print("Waiting for connection...")
                ssocket.listen(2)
        except BaseException as e:
                print("server error in listen: " + str(e))


def server_on_accept_connection():
        global connection
        global ssocket
        try:
                connection, address = ssocket.accept()
                print("Accepted Connection.. from:  "+ str(address))
        except BaseException as e:
                print("server error in accept connection: " + str(e))

def server_recieve_data():
        global trials
        global connection
        try:
                data = connection.recv(BUFFER_SIZE)
                line = data.decode("utf-8")
                print (str(line))
                return line
        except BaseException as e:
                if(trials < 5):
                        trials = trials + 1
                        print("server recieve data failed: " + str(e) + "\nTrying again " + str(trials) + "/5")
                        #server_recieve_data()
                        return "0<3"
                else:
                        trials = 0
                        close_connection()
                        print("server recieve data failed, resetting connection")
                        server_setup()
                        server_listen()
                        server_on_accept_connection()
                        #server_recieve_data()
                        return "0<3"


#=============================
#Function to recieve data from client
def data_active_or_archive():
        try:
                line = server_recieve_data()
                while(line == "0<3"):
                        line = server_recieve_data()
                        
                if len(line)>10:
                        if(not line.find("longitude") == -1 ) and (not line.find("latitude") == -1):
                                if (line.find("archive") == -1):#use date calculations instead, good for now
                                        write_to_active(line)
                                        return 1
                                else:
                                        begin = line.find("timestamp") + 13
                                        get_archive_date = line[begin : begin+ 20]
                                        archive_date = datetime.strptime(get_archive_date, "%b %d %Y %H:%M:%S")
                                        start_of_week = archive_date - timedelta(days = archive_date.weekday())
                                        cleaned_line = line.replace(", 'archive': 'archive'", '')
                                        write_to_archive(cleaned_line, start_of_week)
                                        return 1
                        else:
                                print("Wrong data recieved.. Ignoring")
                                close_connection()
                                server_setup()
                                server_listen()
                                server_on_accept_connection()
                                #data_active_or_archive()
                                return 0
        except BaseException as e:
                print("data active or archive error: " + str(e))
                close_connection()
                server_setup()
                server_listen()
                server_on_accept_connection()
                #data_active_or_archive()
                return 0

def get_week_name(my_date):
        day = str(my_date)
        print(day)
        dt = datetime.strptime(day, '%Y-%m-%d %H:%M:%S.%f')
        start = dt - timedelta(days = dt.weekday())
        year,week,day = start.isocalendar()
        weekname = datetime.strftime(start, '%m-%d-%Y')
        return weekname

#=============================
#Function to write to active file
def write_to_active(data):
        try:
                weekname = get_week_name(datetime.today())
                if not(os.path.isdir(active_path)):
                        os.mkdir(active_path, 0o777)
                file_path = active_path + weekname + ".txt"
                os.system('echo %s|sudo -S %s' % ('', 'chmod 777 ' + file_path))
                with open(file_path, 'a') as myfile:
                        myfile.write(str(data) + '\n')
                        myfile.close()
        except BaseException as e:
                print('error in write to active: ' + str(e) )

#=============================
#Function to write to archive file
def write_to_archive(data,archive_date):
        try:
                weekname = get_week_name(archive_date)
                if not(os.path.isdir(archive_path)):
                        os.mkdir(archive_path, 0o777)
                file_path = archive_path + weekname + ".txt"
                os.system('echo %s|sudo -S %s' % ('', 'chmod 777 ' + file_path))
                with open(file_path, 'a') as myfile:
                        myfile.write(str(data) + '\n')
                        myfile.close()
        except BaseException as e:
                print('error in write to archive: ' + str(e) )

#=============================
#---------------------------------------------not used
#Function to log errors
def print_log(err):
        try:
                err_time = str(datetime.now())
                err_file_path = '/home/ubuntu/city-meter/pm-sensor/error-log.txt'
                os.system('echo %s|sudo -S %s' % ('','chmod 777 ' + err_file_path))
                with open(err_file_path, 'a') as myfile:
                        myfile.write(str(err) + " at: " + err_time + "\n")
                        myfile.close()
        except BaseException as e:
                print("error log error : " + str(e))

#=============================
#Function to close connections open
def close_connection():
        try:
                if not (connection == None) :
                        connection.shutdown(socket.SHUT_RDWR)
                        connection.close()
                if not (ssocket == None) :
                        ssocket.shutdown(socket.SHUT_RDWR)
                        ssocket.close()
                print("closing connection")
        except BaseException as e:
                print("server error in close connection: " + str(e))

#=============================
#function to restart script
def restart_script():
        python= sys.executable
        os.execl(python, python, *  sys.argv)


#=============================
#Function to handle exiting the script
def on_exit_handler(arg1= None, arg2= None):
        close_connection()
        time.sleep(1)
        server_setup()
        server_listen()
        server_on_accept_connection()
        while True:
                data_active_or_archive()
        #restart_script()
        
#=============================
#Function to handle termination and stopping
def set_exit_handler(func):
        import signal
        signal.signal(signal.SIGTERM, func)
        signal.signal(signal.SIGTSTP, func)

#=============================
#Main entry point
if __name__== "__main__":
        #set_exit_handler(on_exit_handler)
        try:
                server_setup()
                server_listen()
                server_on_accept_connection()
                #while True:
                for i in range(0,1000):
                        data_active_or_archive()
                print("exiting main")
        except SystemExit as e:
                print("System Exit error: " + str(e) + "\nRestarting Script")
                wait(1)
                #restart_script()
        except BaseException as e:
                print("Unhandled errpr: " + str(e))
                #on_exit_handler()