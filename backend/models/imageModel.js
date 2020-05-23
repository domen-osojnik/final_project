var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var imageSchema = new Schema({
	'path' : String
});

module.exports = mongoose.model('image', imageSchema);
