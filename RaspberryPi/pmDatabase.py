from sqlalchemy import *
from sqlalchemy.orm import *
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, Integer, String

Base = declarative_base()
class PMLine(Base):
    __tablename__ = 'pm25'
    timestamp = Column(String, primary_key=True)
    pm = Column(Integer)
    isSent = Column(Integer)
    indoor = Column(Integer)

    def __repr__(self):
        return "<PMLine(timestamp='%s', pm='%s', isSent='%s', indoor='%s')>" % (self.timestamp, self.pm, self.isSent, self.indoor)

#========================================
#Function to insert data line to DB
def insert_(session, timestamp_, pm25, isSent_, indoor_):
    try:
        timestamp_ = timestamp_.split('.')[0]
        dataline = PMLine(timestamp = timestamp_,pm = pm25, isSent =isSent_, indoor = indoor_ )
        session.add(dataline)
        session.commit()
    except BaseException as e:
        print('row already exists : ' + str(e))

#========================================
#Function to update an entry in the DB
def update_(session, timestamp_):
    try:
        timestamp_ = str(timestamp_)
        fetched_line = session.query(PMLine).filter_by(timestamp = timestamp_).first()
        fetched_line.isSent = 1
        session.commit()
    except BaseException as e:
        print('row update failed' + str(e))
 
#========================================
#Function to get the next unsent row in the database
def select_row_by_sent(session):
    fetched_line = session.query(PMLine).filter_by(isSent = 0).first()
    return fetched_line