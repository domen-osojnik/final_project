var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var eventsSchema = new Schema({
	'long' : Number,
	'lat' : Number,
	'date' : Date,
	'degree' : Number,
	'speed' : Number
});

module.exports = mongoose.model('events', eventsSchema);
