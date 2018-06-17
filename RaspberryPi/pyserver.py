import socket
import time
import os
import sys
from datetime import datetime, timedelta

#=============================
#initializing variables
TCP_IP = '192.168.1.4'
TCP_PORT = 3678
BUFFER_SIZE = 10240
#Create a socket to store the connection made to file
connection = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
#path to store the files
active_path = '/home/saleh/city-meter/active/'
archive_path = '/home/saleh/city-meter/archive/'

#=============================
#Function to set up the server
#Set ip address and port
#Start listening
def server_setup():
	try:
		ssocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		ssocket.bind((TCP_IP, TCP_PORT))
		print("Waiting for connection...")
		ssocket.listen(1)
		connection, address = ssocket.accept()
		print("Accepted Connection.. ")
	except BaseException as e:
		print(str(e))
	return connection

#=============================
#Function to recieve data from client 
def recieve_data():
	data = connection.recv(BUFFER_SIZE)
	line = data.decode("utf-8")
	if (line.find("archive") == -1):
		write_to_active(line)
	else:
		try:
			begin = line.find("timestamp") + 13
			get_archive_date = line[begin : begin+ 20]
			archive_date = datetime.strptime(get_archive_date, "%b %d %Y %H:%M:%S")
			start_of_week = archive_date - timedelta(days = archive_date.weekday())
			cleaned_line = line.replace(", 'archive': 'archive'", '')
			write_to_archive(cleaned_line, start_of_week)
		except BaseException as e:
			print(str(e))
	print("Data = " + data.decode("utf-8"))

#=============================
#Function to write to active file
def write_to_active(data):
	try:
		day = str(datetime.today())
		dt = datetime.strptime(day, '%Y-%m-%d %H:%M:%S.%f')
		start = dt - timedelta(days = dt.weekday())
		year,week,day = start.isocalendar()
		weekname = datetime.strftime(start, '%m-%d-%Y')
		print(weekname)
		if not(os.path.isdir(active_path)):
			os.mkdir(active_path, 0o777)
		file_path = active_path + weekname + ".txt"
		os.system('echo %s|sudo -S %s' % ('', 'chmod 777 ' + file_path))
		with open(file_path, 'a') as myfile:
			myfile.write(str(data) + '\n')
	except BaseException as e:
		print(str(e))

#=============================
#Function to write to archive file
def write_to_archive(data,archive_date):
	try:
		day = str(archive_date)
		dt = datetime.strptime(day, '%Y-%m-%d %H:%M:%S')
		start = dt - timedelta(days = dt.weekday())
		year,week,day = start.isocalendar()
		weekname = datetime.strftime(start, '%m-%d-%Y')
		print(weekname)
		if not(os.path.isdir(archive_path)):
			os.mkdir(archive_path, 0o777)
		file_path = archive_path + weekname + ".txt"
		os.system('echo %s|sudo -S %s' % ('', 'chmod 777 ' + file_path))
		with open(file_path, 'a') as myfile:
			myfile.write(str(data) + '\n')
	except BaseException as e:
		print(str(e))


#=============================
#Function to close connections open
def close_connection():
	connection.close()

#=============================
#function to restart script
def restart_script():
	python= sys.executable
	os.execl(python, python, *  sys.argv)

#=============================
#Function to handle exiting the script
def on_exit_handler():
	close_connection()
	time.sleep(1)
	restart_script()

#=============================
#Function to handle termination and stopping
def set_exit_handler(func):
	import signal
	signal.signal(signal.SIGTERM, func)
	signal.signal(signal.SIGTSTP, func)

#=============================
#Main entry point
if __name__== "__main__":
	set_exit_handler(on_exit_handler)
	try:
		connection = server_setup()
		while True:
			recieve_data()
	except BaseException:
		on_exit_handler()
	except SystemExit:
		on_exit_handler()
