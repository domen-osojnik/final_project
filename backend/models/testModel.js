var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var testSchema = new Schema({
	'msg' : String
});

module.exports = mongoose.model('test', testSchema);
