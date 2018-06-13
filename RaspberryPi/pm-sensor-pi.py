import serial
import datetime
import os
import stat
import json

#Change the serial port access permission
#os.chmod('/dev/serial0', stat.S_IRUSR | stat.S_IRGRP | stat.S_IROTH | stat.S_IWUSR | stat.S_IWGRP | stat.S_IWOTH | stat.S_IXUSR | stat.S_IXGRP | stat.S_IXOTH)
#for sensor channel
command = 'chmod 777 /dev/serial0'
permission_command = os.system('echo %s|sudo -S %s' % ('', command))
#For data storing path
data_store_path = '/home/pi/Desktop/sensor-data/'
data_store_command = 'chmod 777 ' + data_store_path
data_store_permission_command = os.system('echo %s|sudo -S %s' % ('', data_store_command))

#Open a serial communication channel to recieve data from the PM sensor
serial_channel = serial.Serial('/dev/serial0', baudrate=9600, timeout=3)


#Function to check and read data from channel
#First checks if correct datastrem by matching the first two fixed bytes
#The first two bytes are 0x42 and 0x4d
def reader(channel):
    recieved = b''
    while True:
        fixed1 = channel.read()
        if fixed1 == b'\x42':
            fixed2 = channel.read()
            if fixed2 == b'\x4d':
                recieved += fixed1 + fixed2
                recieved += channel.read(28)
                return recieved
            

#Function to extract the data from the read stream
def data_extract(channel):
    recieved = reader(channel)
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
      #Printed results are not needed at this point, commented for future reference 
##    print('===============\n'
##          'time: {}\n'
##          'PM1.0(CF=1): {}\n'
##               '3PM2.5(CF=1): {}\n'
##               'M10 (CF=1): {}\n'
##               'PM1.0 (STD): {}\n'
##               'PM2.5 (STD): {}\n'
##               'PM10  (STD): {}\n'
##               '>0.3um     : {}\n'
##               '>0.5um     : {}\n'
##               '>1.0um     : {}\n'
##               '>2.5um     : {}\n'
##               '>5.0um     : {}\n'
##               '>10um      : {}'.format( result['timestamp'], result['apm10'], result['apm25'], result['apm100'],
##                                        result['pm10'], result['pm25'], result['pm100'],
##                                        result['gt03um'], result['gt05um'], result['gt10um'],result['gt25um'], result['gt50um'], result['gt100um']))
    #List to save the data for sending over bluetooth
    data_list = []
    data_list.append({
        'timestamp': result['timestamp'].strftime("%b %d %Y %H:%M:%S"),
        'pm2.5':result['pm25']
        })#for sending to mobile
    write_to_file(data_list, result['timestamp'])
 
 
#Function to handle writing data to the Pi
#Each year has a folder and each week's reading are saved in a single file
#Each line has a timestamp and the PM 2.5 standard reading from the sensor
def write_to_file(data, date):
    year,week,day = date.isocalendar()
    if not(os.path.isdir(data_store_path + str(year))):
        os.mkdir(data_store_path + str(year),0o777)
    with open(data_store_path+ str(year) +'/' + str(week)+ '.txt', 'a') as outfile:
        outfile.write(str(data) + '\n')
    
    
#main entry point
if __name__== "__main__":
    while True:
        try:
            data_extract(serial_channel)
        except KeyboardInterrupt:
            serial_channel.close()
            break
