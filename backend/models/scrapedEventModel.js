var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var scrapedEventSchema = new Schema({
	'roadmark' : String,
	'type' : String,
	'desc' : String
});

module.exports = mongoose.model('scrapedEvent', scrapedEventSchema);
