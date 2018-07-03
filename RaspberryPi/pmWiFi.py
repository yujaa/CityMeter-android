import requests
import json
import urllib3
import socket
import pmDatabase
        
#=======================================
#initialize variables with the host address and the port address of the server
host_ip, server_port = "ec2-34-229-219-45.compute-1.amazonaws.com", 80
# Initialize a TCP client socket using SOCK_STREAM
client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        
#=======================================
#Function to create client connection to server
def client_connect():
    global client
    global host_ip
    global server_port
    try:
        client.connect((host_ip,server_port))
    except BaseException as e:
        print('Error in client connect : ' + str(e))
        return"0<10"
#=======================================
#Function to send data over client socket connection
def client_send(data):
    global client
    try:
        client.sendall(data.encode())
        print ("Bytes Sent")
    except BaseException as e:
        print('Error in writing to server : ' + str(e))
        return "0<7"
        
#=======================================
#Function to closeclient connection to server
def close_connection():
    global client
    try:
        client.close()
    except BaseException as e:
        print('Error in close connection : ' + str(e))

#=======================================
#Function to check if connected to wifi
def check_internet():
    try:
        http = urllib3.PoolManager()
        response=http.request('GET','https://www.google.com')
        return True
    except BaseException as err:
        print ("wifi is disconnected ")
        return False