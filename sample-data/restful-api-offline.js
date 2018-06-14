// BASE SETUP
// =============================================================================
var express    = require('express');      
var app        = express();                 
var bodyParser = require('body-parser');
var fcsv = require("fast-csv");
var fs = require("fs");

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

var port = process.env.PORT || 9000;        

// ROUTES FOR  API
// =============================================================================
var router = express.Router();              // get an instance of the express Router

var nodes_csv = "./AoT-complete-latest/nodes.csv";
var nodes_csv_header =['node_id','project_id','vsn','address','lat','lon','description'];

var data_csv = "./AoT-complete-latest/data.csv";

var week2_csv = "aot-week1.csv"
var week2_csv_header = ['Node','Timestamp','Parameter','Value_raw','Value_hrf']//TODO

var targetSensorArr = ['no2','spv1840lr5h_b'];
var targetParamArr = ['pm2_5','pm25_atm'];

// test route to make sure everything is working (accessed at GET /api)
router.get('/', function(req, res) {
    res.json({ message: 'hooray! welcome to our api!' });   
});


// on routes that end in /info
// ----------------------------------------------------
////A List of Nodes
router.route('/info/nodes')
    .get(function(req, res) {
        var nodesList={};
        var stream = fs.createReadStream(nodes_csv);
        fcsv
        .fromStream(stream, { headers: nodes_csv_header})
        .on('data', function (data) {
            if (data['node_id'] != 'node_id') {
                var info={};
                info['lat']= data['lat'];
                info['lon']= data['lon'];
                info['address']= data['address'];

                nodesList[data['node_id']] = info;
            }
        })
        .on('end', function(){
           nodesList['total']= Object.keys(nodesList).length;
           res.json(nodesList);
        })
    });

//Information of each node
router.route('/info/nodes/:node_id')
    .get(function(req, res) {
        var info={};
        var stream = fs.createReadStream(nodes_csv);
        line_cnt =0;
        fcsv
        .fromStream(stream, { headers: nodes_csv_header})
        .on('data', function (data) {
            console.log(data['node_id']);
            if (data['node_id'] == req.params.node_id) {
               var sensor=[];
               
               //scan week2.csv
               var wStream = fs.createReadStream(week2_csv);
               new Promise(function(resolve, reject){
                  fcsv
                  .fromStream(wStream, {headers:week2_csv_header})
                  .on('data', function(wData){
                     line_cnt++;
                     if(line_cnt > 50)
                        return;
                     if(wData['Parameter']== targetParamArr[0] && !(sensor.includes('pm2.5:opc_n2')))
                        sensor.push('pm2.5:opc_n2');
                     if(wData['Parameter']== targetParamArr[1] && !(sensor.includes('pm2.5:pms7003')))
                        sensor.push('pm2.5:pms7003');
                     if(wData['Parameter']== 'concentration' && !(sensor.includes('no2')))
                        sensor.push('no2');
                     if(wData['Parameter']== 'intensity' && !(sensor.includes('sound')))
                        sensor.push('sound');
                     // if(data['sensor']== targetSensorArr[0])
                     //    sensor.push('no2');
                     // if(data['sensor']== targetSensorArr[1])
                     //    sensor.push('sound');
                  }) 
                  .on('end', function(){ 
                      info['node_id'] = req.params.node_id;
                      info['lat']= data['lat'];
                      info['lon']= data['lon'];
                      info['address']= data['address'];
                      info['sensor']= sensor;
                      resolve(1);
                  });
               }).then(()=>{
                  res.json(info);
               });
            }
        })
        .on('end', function(){
        })

    });

//A List of nodes sensing sound level/pressure
router.route('/info/sound')
    .get(function(req, res) {
       var line_cnt=0;
        var nodesList=[];
        var info ={};
        var stream = fs.createReadStream(week2_csv);
        fcsv
        .fromStream(stream, { headers: week2_csv_header})
        .on('data', function (data) {
           line_cnt++;
           if(line_cnt>300)
              return;
            console.log(data);
            if (data['Parameter'] == 'intensity' && !(nodesList.includes(data['Node']))) {
                nodesList.push(data['Node']);
            }
        })
        .on('end', function(){
           if(nodesList.length ==0)
                 res.json({message:'NULL'})
           else{
             info['nodes']= nodesList;
             info['total']= nodesList.length;
             res.json(info);
           }
                 
        })

    });

//A List of nodes sensing PM2.5
router.route('/info/pm25')
    .get(function(req, res) {
       var line_cnt=0;
        var nodesList=[];
        var info ={};
        var stream = fs.createReadStream(week2_csv);
        fcsv
        .fromStream(stream, { headers: week2_csv_header})
        .on('data', function (data) {
           line_cnt++;
           if(line_cnt>300)
              return;
            console.log(data);
            if (targetParamArr.includes(data['Parameter']) && !(nodesList.includes(data['Node']))) {
                nodesList.push(data['Node']);
            }
        })
        .on('end', function(){
           if(nodesList.length ==0)
                 res.json({message:'NULL'})
           else{
             info['nodes']= nodesList;
             info['total']= nodesList.length;
             res.json(info);
           }
        })

    });

//A List of nodes sensing NO2
router.route('/info/no2')
    .get(function(req, res) {
       var line_cnt=0;
        var nodesList=[];
        var info ={};
        var stream = fs.createReadStream(week2_csv);
        fcsv
        .fromStream(stream, { headers: week2_csv_header})
        .on('data', function (data) {
           line_cnt++;
           if(line_cnt>300)
              return;
            console.log(data);
            if (data['Parameter'] == 'concentration' && !(nodesList.includes(data['Node']))) {
                nodesList.push(data['Node']);
            }
        })
        .on('end', function(){
           if(nodesList.length ==0)
                 res.json({message:'NULL'})
           else{
             info['nodes']= nodesList;
             info['total']= nodesList.length;
             res.json(info);
           }
        })

    });
    
// on routes that end in /value
// ----------------------------------------------------
//The latest data from a node
router.route('/value/:node_id')
    .get(function(req, res) {
      
    
    });


// REGISTER OUR ROUTES -------------------------------
// all of our routes will be prefixed with /api
app.use('/api', router);

// START THE SERVER
// =============================================================================
app.listen(port); 
console.log('running...');




function convertTimestamptoDate(timestamp)
{
    //date format example: 2017/12/08 23:22:27
    timestamp=timestamp+'';
    var timeArr = timestamp.split(/[\s:/]/);
    timeArr[1] = timeArr[1]-1;
    return new Date(timeArr[0], timeArr[1], timeArr[2]);
}