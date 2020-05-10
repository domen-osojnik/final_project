var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var signTypeScheme = new Schema({
	'naziv' : String,
	'omejitev' : Boolean,
	'stop' : Boolean
});

module.exports = mongoose.model('sign_type', signTypeScheme); //ZIP_ZNAKA
