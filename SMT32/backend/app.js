/*
 *  requires
 */
var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
var indexRouter = require('./routes/index');
var eventsRouter = require('./routes/eventsRoutes');
var recordingsRouter = require('./routes/recordingsRoutes');
var getRecordingsRouter = require('./routes/getRecordingsRoutes');
var scrapedEventRouter = require('./routes/scrapedEventRoutes');
var imageRouter = require('./routes/imageRoutes');
var imuRouter = require('./routes/imuRoutes');

var app = express();


//povezava z bazo
var mongoose = require('mongoose');
//Set up default mongoose connection
var mongoDB = 'mongodb+srv://admin:admin@finalproject-r59cw.mongodb.net/final_project';
//var mongoDB = '127.0.0.1:3000'
mongoose.connect(mongoDB, { useNewUrlParser: true, useUnifiedTopology: true });
// Get Mongoose to use the global promise library
mongoose.Promise = global.Promise;
//Get the default connection
var db = mongoose.connection;

//Bind connection to error event (to get notification of connection errors)
db.on('error', console.error.bind(console, 'MongoDB connection error:'));

//CORS
var cors = require('cors');
var allowedOrigins = ['http://localhost:4200','http://localhost:3000',
                      'http://yourapp.com'];
app.use(cors({
  credentials: true,
  origin: function(origin, callback){
    // allow requests with no origin 
    // (like mobile apps or curl requests)
    if(!origin) return callback(null, true);
    if(allowedOrigins.indexOf(origin) === -1){
      var msg = 'The CORS policy for this site does not ' +
                'allow access from the specified Origin.';
      return callback(new Error(msg), false);
    }
    return callback(null, true);
  }
}));


// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'hbs');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

//scraper
//var scrape = require('./scraper');
//scrape();

/*
 *  routers
 */
app.use('/', indexRouter);
app.use('/events', eventsRouter);
app.use('/recordings', recordingsRouter);
app.use('/getRecordings', getRecordingsRouter);
app.use('/scrapedEvent', scrapedEventRouter);
app.use('/images', imageRouter);
app.use('/imu', imuRouter);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  console.log(err);
  res.send({"msg":"route does not exist"});
  //res.render('error');
});

module.exports = app;
