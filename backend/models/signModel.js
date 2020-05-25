var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var signSchema = new Schema({
	'path' : String,
	'long' : Number,
	'lat' : Number
});

module.exports = mongoose.model('sign', signSchema);
