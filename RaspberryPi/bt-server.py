# file: rfcomm-server.py
# auth: Albert Huang <albert@csail.mit.edu>
# desc: simple demonstration of a server application that uses RFCOMM sockets
#Modified by Nina Sakhnini 

from bluetooth import *
import datetime

#giving permission to use serial profile for the bluetooth connection
command = 'hciconfig hci0 piscan'
serial_profile_bt_command = os.system('echo %s|sudo -S %s' % ('', command))

#For data storing path
data_store_path = '/home/pi/Desktop/sensor-data/'
data_store_command = 'chmod 777 ' + data_store_path
data_store_permission_command = os.system('echo %s|sudo -S %s' % ('', data_store_command))

server_sock=BluetoothSocket( RFCOMM )
server_sock.bind(("",PORT_ANY))
server_sock.listen(1)

port = server_sock.getsockname()[1]

uuid = "00000003-0000-1000-8000-00805f9b34fb"

advertise_service( server_sock, "SampleServer",
                   service_id = uuid,
                   service_classes = [ uuid, SERIAL_PORT_CLASS ],
                   profiles = [ SERIAL_PORT_PROFILE ], 
#                   protocols = [ OBEX_UUID ] 
                    )
                   
print ("Waiting for connection on RFCOMM channel %d" % port)

client_sock, client_info = server_sock.accept()
print ("Accepted connection from ", client_info)

try:
    while True:
        year,week,day = datetime.datetime.now().isocalendar()
        with open(data_store_path+ str(year) +'/' + str(week)+ '.txt') as f:
            content = f.readlines()
            for line in content:
                print(line)
                print(client_sock.send(line))
                print ("sent [%s]" % line)
except IOError:
    pass

print ("disconnected")

client_sock.close()
server_sock.close()
print ("all done")

